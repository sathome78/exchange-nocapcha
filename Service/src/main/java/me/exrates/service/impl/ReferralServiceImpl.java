package me.exrates.service.impl;

import lombok.extern.log4j.Log4j2;
import me.exrates.dao.ReferralLevelDao;
import me.exrates.dao.ReferralTransactionDao;
import me.exrates.dao.ReferralUserGraphDao;
import me.exrates.model.*;
import me.exrates.model.Currency;
import me.exrates.model.dto.onlineTableDto.MyReferralDetailedDto;
import me.exrates.model.enums.ActionType;
import me.exrates.model.enums.NotificationEvent;
import me.exrates.model.enums.OperationType;
import me.exrates.model.enums.TransactionSourceType;
import me.exrates.model.vo.CacheData;
import me.exrates.model.vo.WalletOperationData;
import me.exrates.service.*;
import me.exrates.service.util.Cache;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.*;

import static java.math.BigDecimal.ZERO;
import static java.math.BigDecimal.valueOf;
import static java.util.Objects.isNull;
import static me.exrates.model.enums.OperationType.REFERRAL;
import static me.exrates.model.util.BigDecimalProcessing.doAction;
import static me.exrates.model.vo.WalletOperationData.BalanceType.ACTIVE;

/**
 * @author Denis Savin (pilgrimm333@gmail.com)
 */
@Log4j2
@Service
@PropertySource("classpath:/referral.properties")
public class ReferralServiceImpl implements ReferralService {

    private final ReferralLevelDao referralLevelDao;
    private final ReferralUserGraphDao referralUserGraphDao;
    private final ReferralTransactionDao referralTransactionDao;
    private final WalletService walletService;
    private final UserService userService;
    private final Commission commission;
    private final CompanyWalletService companyWalletService;
    private final NotificationService notificationService;
    /**
     * Maximum amount of percents
     */
    private final BigDecimal HUNDREDTH = valueOf(100L);
    /**
     * URL following format  - xxx/register?ref=
     * where xxx is replaced by the domain name depending on the maven profile
     */
    private
    @Value("${referral.url}")
    String referralUrl;

    @Autowired
    public ReferralServiceImpl(final ReferralLevelDao referralLevelDao,
                               final ReferralUserGraphDao referralUserGraphDao,
                               final ReferralTransactionDao referralTransactionDao,
                               final WalletService walletService,
                               final UserService userService,
                               final CommissionService commissionService,
                               final CompanyWalletService companyWalletService,
                               final NotificationService notificationService) {
        this.referralLevelDao = referralLevelDao;
        this.referralUserGraphDao = referralUserGraphDao;
        this.referralTransactionDao = referralTransactionDao;
        this.walletService = walletService;
        this.userService = userService;
        this.commission = commissionService.getDefaultCommission(REFERRAL);
        this.companyWalletService = companyWalletService;
        this.notificationService = notificationService;
    }

    /**
     * Generates referral reference following format : [3 random digits] Sponsor UserId [3 random digits]
     *
     * @param userEmail Sponsor email
     * @return String contains referral reference
     */
    @Override
    public String generateReferral(final String userEmail) {
        final int userId = userService.getIdByEmail(userEmail);
        int prefix = new Random().nextInt(999 - 100 + 1) + 100;
        int suffix = new Random().nextInt(999 - 100 + 1) + 100;
        return referralUrl + prefix + userId + suffix;
    }

    /**
     * Consuming referral reference generated by {@link ReferralService#generateReferral(String) generateReferral}
     * and extracting Sponsor {@link User#id} UserId
     *
     * @param ref Referral reference
     * @return Optional which contains Sponsor UserId if ref valid or an Optional.empty()
     */
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
    public void processReferral(final ExOrder exOrder, final BigDecimal commissionAmount, Currency currency, int userId) {
        final List<ReferralLevel> levels = referralLevelDao.findAll();
        CompanyWallet cWallet = companyWalletService.findByCurrency(currency);
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
                int walletId = walletService.getWalletId(parent, currency.getId()); // Mutable variable
                if (walletId == 0) { // Wallet is absent, creating new wallet
                    final Wallet wallet = new Wallet();
                    wallet.setActiveBalance(ZERO);
                    wallet.setCurrencyId(currency.getId());
                    wallet.setUser(userService.getUserById(parent));
                    wallet.setReservedBalance(ZERO);
                    walletId = walletService.createNewWallet(wallet); // Changing mutable variable state
                }
                final ReferralTransaction createdRefTransaction = referralTransactionDao.create(referralTransaction);
                final BigDecimal amount = doAction(commissionAmount, level.getPercent(), ActionType.MULTIPLY_PERCENT);
                final WalletOperationData wod = new WalletOperationData();
                wod.setCommissionAmount(this.commission.getValue());
                wod.setCommission(this.commission);
                wod.setAmount(amount);
                wod.setWalletId(walletId);
                wod.setBalanceType(ACTIVE);
                wod.setOperationType(OperationType.INPUT);
                wod.setSourceType(TransactionSourceType.REFERRAL);
                wod.setSourceId(createdRefTransaction.getId());
                walletService.walletBalanceChange(wod);
                companyWalletService.withdrawReservedBalance(cWallet, amount);
                notificationService.createLocalizedNotification(parent, NotificationEvent.IN_OUT, "referral.title",
                        "referral.message", new Object[]{amount, currency.getName()});
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
    public Integer getReferralParentId(int childId) {
        return referralUserGraphDao.getParent(childId);
    }

    /**
     * The number of referral levels is hardcoded in database (table REFERRAL_LEVEL)
     * Creates new level with specified percent. Old level is not removed, but not used anymore.
     * Stored for already committed transactions at that level
     *
     * @param level      modified level
     * @param oldLevelId old level id
     * @param percent    the desired percentage (the total amount of percents on all levels should not exceed 100 )
     *                   either throws IllegalStateException
     * @return id of the newly created level
     */
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

    /**
     * Associates registered user with its sponsor
     *
     * @param childUserId  child UserId
     * @param parentUserId parent UserId
     */
    @Override
    public void bindChildAndParent(final int childUserId, final int parentUserId) {
        referralUserGraphDao.create(childUserId, parentUserId);
    }

    @Override
    public List<MyReferralDetailedDto> findAllMyReferral(CacheData cacheData, String email, Integer offset, Integer limit, Locale locale) {
        List<MyReferralDetailedDto> result = referralTransactionDao.findAllMyRefferal(email, offset, limit, locale);
        if (Cache.checkCache(cacheData, result)) {
            result = new ArrayList<MyReferralDetailedDto>() {{
                add(new MyReferralDetailedDto(false));
            }};
        }
        return result;
    }

    @Override
    public List<MyReferralDetailedDto> findAllMyReferral(String email, Integer offset, Integer limit, Locale locale) {
        return referralTransactionDao.findAllMyRefferal(email, offset, limit, locale);
    }
    
    @Override
    public List<Integer> getChildrenForParentAndBlock(Integer parentId) {
        return referralUserGraphDao.getChildrenForParentAndBlock(parentId);
    }
    
    @Override
    @Transactional
    public void updateReferralParentForChildren(User user) {
        Integer userReferralParentId = getReferralParentId(user.getId());
        if (userReferralParentId == null) {
            userReferralParentId = userService.getCommonReferralRoot().getId();
        }
        log.debug(String.format("Changing ref parent from %s to %s", user.getId(), userReferralParentId));
        referralUserGraphDao.changeReferralParent(user.getId(), userReferralParentId);
    }
}
