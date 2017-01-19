package me.exrates.service.impl;

import me.exrates.dao.CurrencyDao;
import me.exrates.dao.UserDao;
import me.exrates.dao.WalletDao;
import me.exrates.model.*;
import me.exrates.model.dto.MyWalletConfirmationDetailDto;
import me.exrates.model.dto.UserWalletSummaryDto;
import me.exrates.model.dto.mobileApiDto.dashboard.MyWalletsStatisticsApiDto;
import me.exrates.model.dto.onlineTableDto.MyWalletsDetailedDto;
import me.exrates.model.dto.onlineTableDto.MyWalletsStatisticsDto;
import me.exrates.model.enums.*;
import me.exrates.model.util.BigDecimalProcessing;
import me.exrates.model.vo.CacheData;
import me.exrates.model.vo.WalletOperationData;
import me.exrates.service.CommissionService;
import me.exrates.service.CompanyWalletService;
import me.exrates.service.NotificationService;
import me.exrates.service.WalletService;
import me.exrates.service.exception.*;
import me.exrates.service.util.Cache;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static java.math.BigDecimal.ROUND_HALF_UP;
import static java.math.BigDecimal.ZERO;

@Service
@Transactional
public final class WalletServiceImpl implements WalletService {

    private static final int decimalPlaces = 9;
    private static final Logger LOGGER = LogManager.getLogger(WalletServiceImpl.class);

    @Autowired
    private WalletDao walletDao;
    @Autowired
    private CurrencyDao currencyDao;
    @Autowired
    private UserDao userDao;

    @Autowired
    private CommissionService commissionService;

    @Autowired
    private CompanyWalletService companyWalletService;

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private MessageSource messageSource;

    @Override
    public void balanceRepresentation(final Wallet wallet) {
        wallet
                .setActiveBalance(wallet.getActiveBalance());
//				.setScale(currencyService.resolvePrecision(wallet.getName()), ROUND_CEILING));
    }

    @Transactional(readOnly = true)
    @Override
    public List<Wallet> getAllWallets(int userId) {
        final List<Wallet> wallets = walletDao.findAllByUser(userId);
        wallets.forEach(this::balanceRepresentation);
        return wallets;
    }

    @Transactional(readOnly = true)
    @Override
    public List<MyWalletsDetailedDto> getAllWalletsForUserDetailed(CacheData cacheData,
                                                                   String email, Locale locale) {
        List<MyWalletsDetailedDto> result = walletDao.getAllWalletsForUserDetailed(email, locale);
        if (Cache.checkCache(cacheData, result)) {
            result = new ArrayList<MyWalletsDetailedDto>() {{
                add(new MyWalletsDetailedDto(false));
            }};
        }
        return result;
    }

    @Transactional(readOnly = true)
    @Override
    public List<MyWalletsStatisticsDto> getAllWalletsForUserReduced(CacheData cacheData, String email, Locale locale) {
        List<MyWalletsStatisticsDto> result =  walletDao.getAllWalletsForUserReduced(email, locale);
        if (Cache.checkCache(cacheData, result)) {
            result = new ArrayList<MyWalletsStatisticsDto>() {{
                add(new MyWalletsStatisticsDto(false));
            }};
        }
        return result;
    }

    @Override
    public List<Currency> getCurrencyList() {
        return currencyDao.getCurrList();
    }

    @Override
    public int getWalletId(int userId, int currencyId) {
        return walletDao.getWalletId(userId, currencyId);
    }

    @Override
    @Transactional(propagation = Propagation.NESTED)
    public BigDecimal getWalletABalance(int walletId) {
        return walletDao.getWalletABalance(walletId);
    }

    @Override
    @Transactional(propagation = Propagation.NESTED)
    public BigDecimal getWalletRBalance(int walletId) {
        return walletDao.getWalletRBalance(walletId);
    }

    @Transactional(readOnly = true)
    @Override
    public boolean ifEnoughMoney(int walletId, BigDecimal amountForCheck) {
        BigDecimal balance = getWalletABalance(walletId);
        return balance.compareTo(amountForCheck) >= 0;
    }

    @Transactional(propagation = Propagation.NESTED)
    @Override
    public int createNewWallet(Wallet wallet) {
        return walletDao.createNewWallet(wallet);
    }

    @Override
    public int getUserIdFromWallet(int walletId) {
        return walletDao.getUserIdFromWallet(walletId);
    }

    @Override
    @Transactional(readOnly = true)
    public Wallet findByUserAndCurrency(User user, Currency currency) {
        return walletDao.findByUserAndCurrency(user.getId(), currency.getId());
    }

    @Override
    public Wallet create(User user, Currency currency) {
        final Wallet wallet = walletDao.createWallet(user, currency.getId());
        wallet.setName(currency.getName());
        return wallet;
    }

    @Override
    @Transactional(propagation = Propagation.NESTED)
    public void depositActiveBalance(final Wallet wallet, final BigDecimal sum) {
        final BigDecimal newBalance =
                wallet.getActiveBalance().add(sum).setScale(decimalPlaces, ROUND_HALF_UP);
        wallet.setActiveBalance(newBalance);
        walletDao.update(wallet);
    }

    @Override
    @Transactional(propagation = Propagation.NESTED)
    public void withdrawActiveBalance(final Wallet wallet, final BigDecimal sum) {
        final BigDecimal newBalance = wallet.getActiveBalance().subtract(sum).setScale(decimalPlaces, ROUND_HALF_UP);
        if (newBalance.compareTo(ZERO) < 0) {
            throw new NotEnoughUserWalletMoneyException("Not enough money to withdraw on user wallet " +
                    wallet.toString());
        }
        wallet.setActiveBalance(newBalance);
        walletDao.update(wallet);
    }

    @Override
    @Transactional(propagation = Propagation.NESTED)
    public void depositReservedBalance(final Wallet wallet, final BigDecimal sum) {
        wallet.setActiveBalance(wallet.getActiveBalance().subtract(sum).setScale(decimalPlaces, ROUND_HALF_UP));
        if (wallet.getActiveBalance().compareTo(ZERO) < 0) {
            throw new NotEnoughUserWalletMoneyException("Not enough money to withdraw on user wallet " + wallet);
        }
        wallet.setReservedBalance(wallet.getReservedBalance().add(sum).setScale(decimalPlaces, ROUND_HALF_UP));
        walletDao.update(wallet);
    }

    @Override
    @Transactional(propagation = Propagation.NESTED)
    public void withdrawReservedBalance(final Wallet wallet, final BigDecimal sum) {
        wallet.setReservedBalance(wallet.getReservedBalance().subtract(sum).setScale(decimalPlaces, ROUND_HALF_UP));
        if (wallet.getReservedBalance().compareTo(ZERO) < 0) {
            throw new NotEnoughUserWalletMoneyException("Not enough money to withdraw on user wallet " + wallet);
        }
        walletDao.update(wallet);
    }

    public List<UserWalletSummaryDto> getUsersWalletsSummary() {
        return walletDao.getUsersWalletsSummary();
    }

    @Override
    public WalletTransferStatus walletInnerTransfer(int walletId, BigDecimal amount, TransactionSourceType sourceType, int sourceId) {
        return walletDao.walletInnerTransfer(walletId, amount, sourceType, sourceId);
    }

    @Override
    public WalletTransferStatus walletBalanceChange(final WalletOperationData walletOperationData) {
        return walletDao.walletBalanceChange(walletOperationData);
    }

    @Override
    public List<MyWalletConfirmationDetailDto> getWalletConfirmationDetail(Integer walletId, Locale locale) {
        return walletDao.getWalletConfirmationDetail(walletId, locale);
    }

    @Override
    @Transactional(readOnly = true)
    public MyWalletsStatisticsApiDto getUserWalletShortStatistics(int walletId) {
        return walletDao.getWalletShortStatistics(walletId);
    }

     /*
    * Methods defined below are overloaded versions of dashboard info supplier methods.
    * They are supposed to use with REST API which is stateless and cannot use session-based caching.
    * */


    @Transactional(readOnly = true)
    @Override
    public List<MyWalletsDetailedDto> getAllWalletsForUserDetailed(String email, List<Integer> currencyIds, Locale locale) {
        return walletDao.getAllWalletsForUserDetailed(email, currencyIds, locale);
    }

    @Transactional(readOnly = true)
    @Override
    public List<MyWalletsStatisticsDto> getAllWalletsForUserReduced(String email, Locale locale) {
        return walletDao.getAllWalletsForUserReduced(email, locale);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void manualBalanceChange(Integer userId, Integer currencyId, BigDecimal amount) {
        if (amount.equals(BigDecimal.ZERO)) {
            return;
        }
        Wallet wallet = walletDao.findByUserAndCurrency(userId, currencyId);
        if (amount.signum() == -1 && amount.abs().compareTo(wallet.getActiveBalance()) > 0) {
            throw new InvalidAmountException("Negative amount exceeds current balance!");
        }
         changeWalletActiveBalance(amount, wallet, OperationType.MANUAL, TransactionSourceType.MANUAL);

    }

    private void changeWalletActiveBalance(BigDecimal amount, Wallet wallet, OperationType operationType,
                                                           TransactionSourceType transactionSourceType) {
        changeWalletActiveBalance(amount, wallet, operationType, transactionSourceType, null);
    }

    private void changeWalletActiveBalance(BigDecimal amount, Wallet wallet, OperationType operationType,
                                           TransactionSourceType transactionSourceType, BigDecimal specialCommissionAmount) {
        WalletOperationData walletOperationData = new WalletOperationData();
        walletOperationData.setWalletId(wallet.getId());
        walletOperationData.setAmount(amount);
        walletOperationData.setBalanceType(WalletOperationData.BalanceType.ACTIVE);
        walletOperationData.setOperationType(operationType);
        Commission commission = commissionService.findCommissionByType(operationType);
        walletOperationData.setCommission(commission);
        BigDecimal commissionAmount = specialCommissionAmount == null ?
                BigDecimalProcessing.doAction(amount, commission.getValue(), ActionType.MULTIPLY_PERCENT) : specialCommissionAmount;
        walletOperationData.setCommissionAmount(commissionAmount);
        walletOperationData.setSourceType(transactionSourceType);
        WalletTransferStatus status = walletBalanceChange(walletOperationData);
        if (status != WalletTransferStatus.SUCCESS) {
            throw new BalanceChangeException(status.name());
        }
        if (commissionAmount.signum() > 0) {

           CompanyWallet companyWallet = companyWalletService.findByCurrency(currencyDao.findById(wallet.getCurrencyId()));
           companyWalletService.deposit(companyWallet, BigDecimal.ZERO, commissionAmount);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public String transferCostsToUser(Integer fromUserWalletId, String toUserNickname, Integer currencyId, BigDecimal amount, Locale locale) {
        if (amount.signum() <= 0) {
            throw new InvalidAmountException(messageSource.getMessage("transfer.negativeAmount", null, locale));
        }

        Wallet fromUserWallet =  walletDao.findById(fromUserWalletId);
        Commission commission = commissionService.findCommissionByType(OperationType.USER_TRANSFER);
        BigDecimal commissionAmount = BigDecimalProcessing.doAction(amount, commission.getValue(), ActionType.MULTIPLY_PERCENT);
        BigDecimal totalAmount = amount.add(commissionAmount);
        if (totalAmount.compareTo(fromUserWallet.getActiveBalance()) > 0) {
            throw new InvalidAmountException(messageSource.getMessage("transfer.invalidAmount", null, locale));
        }
        Integer userId = userDao.getIdByNickname(toUserNickname);
        if (userId == 0) {
            throw new UserNotFoundException(messageSource.getMessage("transfer.userNotFound", new Object[]{toUserNickname}, locale));
        }

        Wallet toUserWallet =  walletDao.findByUserAndCurrency(userId, currencyId);
        if (toUserWallet == null) {
            throw new WalletNotFoundException(messageSource.getMessage("transfer.walletNotFound", null, locale));
        }
        changeWalletActiveBalance(totalAmount, fromUserWallet, OperationType.OUTPUT,
                TransactionSourceType.USER_TRANSFER, commissionAmount);
        changeWalletActiveBalance(amount, toUserWallet, OperationType.INPUT,
                TransactionSourceType.USER_TRANSFER, BigDecimal.ZERO);
        String currencyName = currencyDao.getCurrencyName(currencyId);
        String result = messageSource.getMessage("transfer.successful", new Object[]{amount, currencyName, toUserNickname},
                locale);
        notificationService.notifyUser(fromUserWallet.getUser().getId(), NotificationEvent.IN_OUT, "wallets.transferTitle",
                "transfer.successful", new Object[]{amount, currencyName, toUserNickname});
        notificationService.notifyUser(toUserWallet.getUser().getId(), NotificationEvent.IN_OUT, "wallets.transferTitle",
                "transfer.received", new Object[]{amount, currencyName});
        return result;
    }





}
