package me.exrates.service.impl;

import me.exrates.dao.ReferralLevelDao;
import me.exrates.dao.ReferralTransactionDao;
import me.exrates.dao.ReferralUserGraphDao;
import me.exrates.model.Commission;
import me.exrates.model.ExOrder;
import me.exrates.model.ReferralLevel;
import me.exrates.model.ReferralTransaction;
import me.exrates.model.User;
import me.exrates.model.Wallet;
import me.exrates.model.enums.ActionType;
import me.exrates.model.enums.OperationType;
import me.exrates.model.enums.TransactionSourceType;
import me.exrates.model.util.BigDecimalProcessing;
import me.exrates.model.vo.WalletOperationData;
import me.exrates.service.CommissionService;
import me.exrates.service.ReferralService;
import me.exrates.service.UserService;
import me.exrates.service.WalletService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.Random;

import static java.math.BigDecimal.ZERO;
import static java.math.BigDecimal.valueOf;
import static java.util.Objects.isNull;
import static me.exrates.model.enums.OperationType.REFERRAL;
import static me.exrates.model.vo.WalletOperationData.BalanceType.RESERVED;

/**
 * @author Denis Savin (pilgrimm333@gmail.com)
 */
@Service
@PropertySource("classpath:/referral.properties")
public class ReferralServiceImpl implements ReferralService {

    private @Value("${referral.url}") String referralUrl;

    private final ReferralLevelDao referralLevelDao;
    private final ReferralUserGraphDao referralUserGraphDao;
    private final ReferralTransactionDao referralTransactionDao;
    private final WalletService walletService;
    private final UserService userService;
    private final Commission commission;

    /**
     * Maximum amount of percents
     */
    private final BigDecimal HUNDREDTH = valueOf(100L);

    @Autowired
    public ReferralServiceImpl(final ReferralLevelDao referralLevelDao,
                               final ReferralUserGraphDao referralUserGraphDao,
                               final ReferralTransactionDao referralTransactionDao,
                               final WalletService walletService,
                               final UserService userService,
                               final CommissionService commissionService)
    {
        this.referralLevelDao = referralLevelDao;
        this.referralUserGraphDao = referralUserGraphDao;
        this.referralTransactionDao = referralTransactionDao;
        this.walletService = walletService;
        this.userService = userService;
        this.commission = commissionService.findCommissionByType(REFERRAL);
    }

    @Override
    public String generateReferral(final String userEmail) {
        final int userId = userService.getIdByEmail(userEmail);
        int prefix = new Random().nextInt(999 - 100 + 1) + 100;
        int suffix = new Random().nextInt(999 - 100 + 1) + 100;
        return referralUrl + prefix + userId + suffix;
    }

    @Override
    public Optional<Integer> reduceReferralRef(final String ref) {
        final String id = ref.substring(3).substring(0, ref.length() - 6);
        if (id.matches("[0-9]+")) {
            return Optional.of(Integer.valueOf(id));
        }
        return Optional.empty();
    }

    @Override
    @Transactional(propagation = Propagation.MANDATORY)
    public void processReferral(final ExOrder exOrder, final BigDecimal commissionAmount, int currencyId, int userId) {
        final List<ReferralLevel> levels = referralLevelDao.findAll();
        Integer parent = null;
        for (ReferralLevel level : levels) {
            if (parent == null) {
                parent = referralUserGraphDao.getParent(userId);
            } else {
                parent = referralUserGraphDao.getParent(parent);
            }
            if (parent != null && !level.getPercent().equals(ZERO)) {
                final ReferralTransaction referralTransaction = new ReferralTransaction();
                referralTransaction.setExOrder(exOrder);
                referralTransaction.setReferralLevel(level);
                referralTransaction.setUserId(parent);
                referralTransaction.setInitiatorId(userId);
                int walletId = walletService.getWalletId(parent, currencyId); // Mutable variable
                if (walletId == 0) { // Wallet is absent, creating new wallet
                    final Wallet wallet = new Wallet();
                    wallet.setActiveBalance(ZERO);
                    wallet.setCurrencyId(currencyId);
                    wallet.setUserId(parent);
                    wallet.setReservedBalance(ZERO);
                    walletId = walletService.createNewWallet(wallet); // Changing mutable variable state
                }
                final ReferralTransaction createdReferralTransaction = referralTransactionDao.create(referralTransaction);
                final BigDecimal amount = BigDecimalProcessing.doAction(commissionAmount, level.getPercent(), ActionType.MULTIPLY_PERCENT);
                final WalletOperationData wod = new WalletOperationData();
                wod.setCommissionAmount(this.commission.getValue());
                wod.setCommission(this.commission);
                wod.setAmount(amount);
                wod.setWalletId(walletId);
                wod.setBalanceType(RESERVED);
                wod.setOperationType(OperationType.REFERRAL);
                wod.setSourceType(TransactionSourceType.REFERRAL);
                wod.setSourceId(createdReferralTransaction.getId());
                walletService.walletBalanceChange(wod);
            } else {
                break;
            }
        }
    }

    @Override
    public List<ReferralTransaction> findAll(final int userId) {
        return referralTransactionDao.findAll(userId);
    }

    @Override
    public List<ReferralLevel> findAllReferralLevels() {
        return referralLevelDao.findAll();
    }

    @Override
    public String getParentEmail(final int childId) {
        final Integer parent = referralUserGraphDao.getParent(childId);
        final User user = userService.getUserById(parent);
        if (!isNull(user)) {
            return user.getEmail();
        }
        return null;
    }

    @Override
    public int updateReferralLevel(final int level, final int oldLevelId, final BigDecimal percent) {
        final BigDecimal oldLevelPercent = referralLevelDao.findById(oldLevelId).getPercent();
        if (referralLevelDao.getTotalLevelsPercent().subtract(oldLevelPercent).add(percent).compareTo(HUNDREDTH) > 0) {
            throw new IllegalStateException("The total amount of percents at all levels should not exceed 100%");
        }
        if (percent.compareTo(ZERO) < 0) {
            throw new IllegalArgumentException("Percent should be positive");
        }
        final ReferralLevel referralLevel = new ReferralLevel();
        referralLevel.setLevel(level);
        referralLevel.setPercent(percent);
        return referralLevelDao.create(referralLevel);
    }

    @Override
    public void bindChildAndParent(final int childUserId, final int parentUserId) {
        referralUserGraphDao.create(childUserId, parentUserId);
    }
}
