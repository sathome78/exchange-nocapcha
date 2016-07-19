package me.exrates.service.impl;

import me.exrates.dao.CurrencyDao;
import me.exrates.dao.WalletDao;
import me.exrates.model.Currency;
import me.exrates.model.User;
import me.exrates.model.Wallet;
import me.exrates.model.dto.MyWalletConfirmationDetailDto;
import me.exrates.model.dto.UserWalletSummaryDto;
import me.exrates.model.dto.onlineTableDto.MyWalletsDetailedDto;
import me.exrates.model.dto.onlineTableDto.MyWalletsStatisticsDto;
import me.exrates.model.enums.TransactionSourceType;
import me.exrates.model.enums.WalletTransferStatus;
import me.exrates.model.vo.CacheData;
import me.exrates.model.vo.WalletOperationData;
import me.exrates.service.WalletService;
import me.exrates.service.exception.NotEnoughUserWalletMoneyException;
import me.exrates.service.util.Cache;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
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
        final Wallet wallet = walletDao.createWallet(user.getId(), currency.getId());
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
}
