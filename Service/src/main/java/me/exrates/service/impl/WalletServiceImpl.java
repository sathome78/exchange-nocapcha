package me.exrates.service.impl;

import me.exrates.dao.CurrencyDao;
import me.exrates.dao.WalletDao;
import me.exrates.model.Currency;
import me.exrates.model.User;
import me.exrates.model.Wallet;
import me.exrates.service.CommissionService;
import me.exrates.service.WalletService;
import me.exrates.service.exception.NotEnoughUserWalletMoneyException;
import me.exrates.service.exception.WalletPersistException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@Transactional
public class WalletServiceImpl implements WalletService {

	@Autowired
	WalletDao walletDao;

	@Autowired
	CommissionService commissionService;

	@Autowired
	CurrencyDao currencyDao;

	private static final Logger logger = LogManager.getLogger(WalletServiceImpl.class);

	@Transactional(readOnly = true)
	@Override
	public List<Wallet> getAllWallets(int userId) {
		return walletDao.findAllByUser(userId);
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
		if(balance.compareTo(amountForCheck) >= 0){
			return true;
		}
		else return false;
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
		return walletDao.findByUserAndCurrency(user.getId(),currency.getId());
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
		final BigDecimal newBalance = oldBalance.add(amount).setScale(9,BigDecimal.ROUND_CEILING);
		if(newBalance.signum() == -1) {
			return false;
		}
		else return walletDao.setWalletABalance(walletId, newBalance);
	}
	
	@Transactional(propagation = Propagation.NESTED)
	@Override
	public boolean setWalletRBalance(int walletId, BigDecimal amount) {
		final BigDecimal oldBalance = walletDao.getWalletRBalance(walletId);
		final BigDecimal newBalance = oldBalance.add(amount).setScale(9,BigDecimal.ROUND_CEILING);
		if(newBalance.signum() == -1) {
			return false;
		}
		else return walletDao.setWalletRBalance(walletId, newBalance);
	}


	@Override
	@Transactional(propagation = Propagation.NESTED)
	public void depositActiveBalance(Wallet wallet, BigDecimal sum) {
		logger.info("Trying deposit active balance on wallet "+ wallet+
			", amount: "+sum);
		final BigDecimal newBalance =
				wallet.getActiveBalance().add(sum).setScale(9,BigDecimal.ROUND_CEILING);
		wallet.setActiveBalance(newBalance);
		if (!walletDao.update(wallet)) {
			throw new WalletPersistException("Failed to deposit on user wallet " + wallet.toString());
		}
		logger.info("Successfull active balance deposit on wallet "+wallet);
	}

	@Override
	@Transactional(propagation = Propagation.NESTED)
	public void withdrawActiveBalance(Wallet wallet, BigDecimal sum) {
		logger.info("Trying withdraw active balance on wallet "+ wallet+
			", amount: "+sum);
		final BigDecimal newBalance =
				wallet.getActiveBalance().subtract(sum).setScale(9,BigDecimal.ROUND_CEILING);
		if (newBalance.compareTo(BigDecimal.ZERO) < 0) {
			throw new NotEnoughUserWalletMoneyException("Not enough money to withdraw on user wallet " + wallet.toString());
		}
		wallet.setActiveBalance(newBalance);
		if (!walletDao.update(wallet)) {
			throw new WalletPersistException("Failed to withdraw on user wallet " + wallet.toString());
		}
		logger.info("Successfull active balance withdraw on wallet "+wallet);
	}

	@Override
	@Transactional(propagation = Propagation.NESTED)
	public void depositReservedBalance(Wallet wallet, BigDecimal sum) {
		throw new UnsupportedOperationException();
	}

	@Override
	@Transactional(propagation = Propagation.NESTED)
	public void withdrawReservedBalance(Wallet wallet, BigDecimal sum) {
		throw new UnsupportedOperationException();
	}
}