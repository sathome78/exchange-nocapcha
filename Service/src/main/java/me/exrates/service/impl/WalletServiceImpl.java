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
	public double getWalletABalance(int walletId) {
		return walletDao.getWalletABalance(walletId);
	}

	@Override
	public double getWalletRBalance(int walletId) {
		return walletDao.getWalletRBalance(walletId);
	}



	//// TODO: 2/23/16 Dmytro Sokolov Replace to withdraw() and deposit() methods
	@Transactional
	@Override
	public boolean setWalletABalance(int walletId, double amount) {
		System.out.println(amount + " AMOUNT");
		final double oldBalance = walletDao.getWalletABalance(walletId);
		System.out.println(oldBalance + "OLD BALANCE");
		final BigDecimal newBalance = BigDecimal.valueOf(oldBalance).add(BigDecimal.valueOf(amount)).setScale(9,BigDecimal.ROUND_CEILING);
		System.out.println(newBalance + " NEW BALANCE");
		if(newBalance.signum() == -1) {
			return false;
		}
		else return walletDao.setWalletABalance(walletId, newBalance.doubleValue());
	}

	@Override
	public int getCurrencyId(int walletId) {
		return currencyDao.getCurrencyId(walletId);
	}

	@Override
	public String getCurrencyName(int currencyId) {
		return currencyDao.getCurrencyName(currencyId);
	}

	//// TODO: 2/23/16 Dmytro Sokolov Replace to withdraw() and deposit() methods
	@Transactional
	@Override
	public boolean setWalletRBalance(int walletId, double amount) {
		final double oldBalance = walletDao.getWalletRBalance(walletId);
		final BigDecimal newBalance = BigDecimal.valueOf(oldBalance).add(BigDecimal.valueOf(amount)).setScale(9,BigDecimal.ROUND_CEILING);
		if(newBalance.signum() == -1) {
			return false;
		}
		else return walletDao.setWalletRBalance(walletId, newBalance.doubleValue());
	}

	@Transactional(readOnly = true)
	@Override
	public boolean ifEnoughMoney(int walletId, double amountForCheck) {
		double balance = getWalletABalance(walletId);
		if(balance >= amountForCheck){
			return true;
		}
		else return false;
	}

	@Transactional
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

	@Override
	@Transactional(propagation = Propagation.MANDATORY)
	public void depositActiveBalance(Wallet wallet, BigDecimal sum) {
		final BigDecimal newBalance =
				BigDecimal.valueOf(wallet.getActiveBalance()).add(sum).setScale(9,BigDecimal.ROUND_CEILING);
		wallet.setActiveBalance(newBalance.doubleValue());
		if (!walletDao.update(wallet))
			throw new WalletPersistException("Failed to deposit on user wallet " + wallet.toString());
	}

	@Override
	@Transactional(propagation = Propagation.MANDATORY)
	public void withdrawActiveBalance(Wallet wallet, BigDecimal sum) {
		final BigDecimal newBalance =
				BigDecimal.valueOf(wallet.getActiveBalance()).subtract(sum).setScale(9,BigDecimal.ROUND_CEILING);
		if (newBalance.compareTo(BigDecimal.ZERO) < 0) {
			throw new NotEnoughUserWalletMoneyException("Not enough money to withdraw on user wallet " + wallet.toString());
		}
		wallet.setActiveBalance(newBalance.doubleValue());
		if (!walletDao.update(wallet)) {
			throw new WalletPersistException("Failed to withdraw on user wallet " + wallet.toString());
		}
	}

	@Override
	@Transactional(propagation = Propagation.MANDATORY)
	public void depositReservedBalance(Wallet wallet, BigDecimal sum) {
		throw new UnsupportedOperationException();
	}

	@Override
	@Transactional(propagation = Propagation.MANDATORY)
	public void withdrawReservedBalance(Wallet wallet, BigDecimal sum) {
		throw new UnsupportedOperationException();
	}
}