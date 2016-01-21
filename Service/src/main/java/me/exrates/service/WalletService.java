package me.exrates.service;

import java.util.List;

import me.exrates.model.Currency;
import me.exrates.model.Wallet;

public interface WalletService {

	public List<Wallet> getAllWallets(int userId); 
	
	public List<Currency> getCurrencyList();
	
	public int getWalletId(int userId, int currencyId);
	
	public double getWalletABalance(int walletId);
	
	public double getWalletRBalance(int walletId);
	
	public boolean ifEnoughMoney(int walletId, double amountForCheck, int operationType);
}
