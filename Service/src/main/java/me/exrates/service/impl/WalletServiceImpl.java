package me.exrates.service.impl;

import java.math.BigDecimal;
import java.util.List;

import me.exrates.service.CommissionService;
import me.exrates.service.WalletService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import me.exrates.dao.CurrencyDao;
import me.exrates.dao.WalletDao;
import me.exrates.model.Currency;
import me.exrates.model.Wallet;
import org.springframework.transaction.annotation.Transactional;

@Service("walletService")
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
		return walletDao.getAllWallets(userId);
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

	@Transactional()
	@Override
	public boolean setWalletABalance(int walletId, double amount) {
		final double oldBalance = walletDao.getWalletABalance(walletId);
		final BigDecimal newBalance = BigDecimal.valueOf(oldBalance).add(BigDecimal.valueOf(amount)).setScale(9,BigDecimal.ROUND_CEILING);
		return walletDao.setWalletABalance(walletId, newBalance.doubleValue());
	}

	@Override
	public int getCurrencyId(int walletId) {
		return currencyDao.getCurrencyId(walletId);
	}

	@Override
	public String getCurrencyName(int currencyId) {
		return currencyDao.getCurrencyName(currencyId);
	}

	@Transactional
	@Override
	public boolean setWalletRBalance(int walletId, double amount) {
		final double oldBalance = walletDao.getWalletRBalance(walletId);
		final BigDecimal newBalance = BigDecimal.valueOf(oldBalance).add(BigDecimal.valueOf(amount)).setScale(9,BigDecimal.ROUND_CEILING);
		return walletDao.setWalletRBalance(walletId, newBalance.doubleValue());
	}

	@Transactional(readOnly = true)
	@Override
	public boolean ifEnoughMoney(int walletId, double amountForCheck) {
		double balance = getWalletABalance(walletId);
		if(balance > amountForCheck){
			return true;
		}
		else return false;
	}
}