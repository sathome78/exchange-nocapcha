package me.exrates.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import me.exrates.dao.CurrencyDao;
import me.exrates.dao.WalletDao;
import me.exrates.model.Currency;
import me.exrates.model.Wallet;

@Service("walletService")
public class WalletServiceImpl implements WalletService{

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
		for(Wallet wallet : walletList) {
			for(Currency currency : currList) {
				if(wallet.getCurrId() == currency.getId()) {
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
	public boolean setWalletABalance(int walletId, double newBalance) {
		return walletDao.setWalletABalance(walletId,newBalance);
	}

	@Override
	public boolean setWalletRBalance(int walletId, double newBalance) {
		return walletDao.setWalletRBalance(walletId,newBalance);
	}

	@Override
	public boolean ifEnoughMoney(int walletId, double amountForCheck, int operationType) {
		double balance = getWalletABalance(walletId);
		double commission = commissionService.getCommissionByType(operationType);
		double sumForCheck = amountForCheck + amountForCheck*commission/100; 
		if(balance > sumForCheck){
			return true;
		}
		else return false;
	}
	
	
	
	
	
	

}
