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

@Service("walletService")
public class WalletServiceImpl implements WalletService {

	@Autowired
	WalletDao walletDao;

	@Autowired
	CommissionService commissionService;

	@Autowired
	CurrencyDao currencyDao;

	@Override
	public List<Wallet> getAllWallets(int userId) {
		List<Wallet> walletList = walletDao.getAllWallets(userId);
		List<Currency> currList = currencyDao.getCurrList();
		for (Wallet wallet : walletList) {
			for (Currency currency : currList) {
				if (wallet.getCurrId() == currency.getId()) {
					wallet.setName(currency.getName());
				}
			}
		}
		return walletList;
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

	@Override
	public boolean setWalletABalance(int walletId, double amount) {
		final double oldBalance = walletDao.getWalletABalance(walletId);
		final double newBalance = Math.round((oldBalance + amount) * 100.00) / 100.00;
		return walletDao.setWalletABalance(walletId, BigDecimal.valueOf(newBalance));
	}

	@Override
	public boolean setWalletRBalance(int walletId, double amount) {
		final double oldBalance = walletDao.getWalletRBalance(walletId);
		final double newBalance = Math.round((oldBalance + amount) * 100.00) / 100.00;
		return walletDao.setWalletRBalance(walletId, BigDecimal.valueOf(newBalance));
	}

	@Override
	public boolean ifEnoughMoney(int walletId, double amountForCheck, int operationType) {
		double balance = getWalletABalance(walletId);
		double commission = commissionService.getCommissionByType(operationType);
		double sumForCheck = amountForCheck + amountForCheck * commission / 100;
		return balance > sumForCheck;
	}
}