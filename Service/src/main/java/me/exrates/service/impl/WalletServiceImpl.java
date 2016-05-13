package me.exrates.service.impl;

import me.exrates.dao.CurrencyDao;
import me.exrates.dao.WalletDao;
import me.exrates.model.Currency;
import me.exrates.model.User;
import me.exrates.model.Wallet;
import me.exrates.model.dto.UsersWalletsDto;
import me.exrates.model.dto.UsersWalletsSummaryDto;
import me.exrates.model.enums.ActionType;
import me.exrates.model.util.BigDecimalProcessing;
import me.exrates.service.CurrencyService;
import me.exrates.service.WalletService;
import me.exrates.service.exception.NotEnoughUserWalletMoneyException;
import me.exrates.service.exception.OperationCausedNegativeBalance;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.MathContext;
import java.util.List;

import static java.math.BigDecimal.ZERO;
import static java.math.RoundingMode.CEILING;

@Service
@Transactional
public final class WalletServiceImpl implements WalletService {

    private static final MathContext MATH_CONTEXT = new MathContext(9, CEILING);
    private static final Logger LOGGER = LogManager.getLogger(WalletServiceImpl.class);
    @Autowired
    private WalletDao walletDao;
    @Autowired
    private CurrencyDao currencyDao;
    @Autowired
    private CurrencyService currencyService;

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

    @Override
    public int getCurrencyId(int walletId) {
        return currencyDao.getCurrencyId(walletId);
    }

    @Override
    public String getCurrencyName(int currencyId) {
        return currencyDao.getCurrencyName(currencyId);
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

    @Transactional(propagation = Propagation.NESTED)
    @Override
    public boolean setWalletABalance(int walletId, BigDecimal amount) {
        final BigDecimal oldBalance = walletDao.getWalletABalance(walletId);
        final BigDecimal newBalance = BigDecimalProcessing.doAction(oldBalance, amount, ActionType.ADD);
        if (newBalance.signum() == -1) {
            throw new OperationCausedNegativeBalance(String.format("Operation of amount %s caused a negative active balance on wallet: %s", amount.toPlainString(), String.valueOf(walletId)));
        }
        return walletDao.setWalletABalance(walletId, newBalance);
    }

    @Transactional(propagation = Propagation.NESTED)
    @Override
    public boolean setWalletRBalance(int walletId, BigDecimal amount) {
        final BigDecimal oldBalance = walletDao.getWalletRBalance(walletId);
        final BigDecimal newBalance = BigDecimalProcessing.doAction(oldBalance, amount, ActionType.ADD);
        if (newBalance.signum() == -1) {
            throw new OperationCausedNegativeBalance(String.format("Operation of amount %s caused a negative reserved balance on wallet: %s", amount.toPlainString(), String.valueOf(walletId)));
        }
        return walletDao.setWalletRBalance(walletId, newBalance);
    }


    @Override
    @Transactional(propagation = Propagation.NESTED)
    public void depositActiveBalance(final Wallet wallet, final BigDecimal sum) {
        LOGGER.info("Trying deposit active balance on wallet  " + wallet +
                ", amount: " + sum);
        final BigDecimal newBalance =
                wallet.getActiveBalance().add(sum, MATH_CONTEXT);
        wallet.setActiveBalance(newBalance);
        walletDao.update(wallet);
        LOGGER.info("Successful active balance deposit on wallet " + wallet);
    }

    @Override
    @Transactional(propagation = Propagation.NESTED)
    public void withdrawActiveBalance(final Wallet wallet, final BigDecimal sum) {
        LOGGER.info("Trying withdraw active balance on wallet " + wallet +
                ", amount: " + sum);
        final BigDecimal newBalance = wallet.getActiveBalance().subtract(sum, MATH_CONTEXT);
        if (newBalance.compareTo(ZERO) < 0) {
            throw new NotEnoughUserWalletMoneyException("Not enough money to withdraw on user wallet " +
                    wallet.toString());
        }
        wallet.setActiveBalance(newBalance);
        walletDao.update(wallet);
        LOGGER.info("Successful active balance withdraw on wallet " + wallet);
    }

    @Override
    @Transactional(propagation = Propagation.NESTED)
    public void depositReservedBalance(final Wallet wallet, final BigDecimal sum) {
        LOGGER.info("Trying deposit reserved balance on wallet " + wallet +
                ", amount: " + sum);
        wallet.setActiveBalance(wallet.getActiveBalance().subtract(sum, MATH_CONTEXT));
        if (wallet.getActiveBalance().compareTo(ZERO) < 0) {
            throw new NotEnoughUserWalletMoneyException("Not enough money to withdraw on user wallet " + wallet);
        }
        wallet.setReservedBalance(wallet.getReservedBalance().add(sum, MATH_CONTEXT));
        walletDao.update(wallet);
        LOGGER.info("Successful reserved balance deposit on wallet " + wallet);
    }

    @Override
    @Transactional(propagation = Propagation.NESTED)
    public void withdrawReservedBalance(final Wallet wallet, final BigDecimal sum) {
        LOGGER.info("Trying withdraw reserved balance on wallet " + wallet + ", amount: " + sum);
        wallet.setReservedBalance(wallet.getReservedBalance().subtract(sum, MATH_CONTEXT));
        if (wallet.getReservedBalance().compareTo(ZERO) < 0) {
            throw new NotEnoughUserWalletMoneyException("Not enough money to withdraw on user wallet " + wallet);
        }
        walletDao.update(wallet);
        LOGGER.info("Successful reserved balance deposit on wallet " + wallet);
    }

    public List<UsersWalletsSummaryDto> getUsersWalletsSummary() {
        return walletDao.getUsersWalletsSummary();
    }

    public List<UsersWalletsDto> getUsersWalletsList() {
        return walletDao.getUsersWalletsList();
    }
}
