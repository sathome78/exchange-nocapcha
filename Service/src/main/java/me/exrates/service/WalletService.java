package me.exrates.service;

import java.util.List;

import me.exrates.model.Currency;
import me.exrates.model.Wallet;

public interface WalletService {

<<<<<<< HEAD
	public List<Wallet> getAllWallets(int userId); 
	
	public List<Currency> getCurrencyList();
	
	public int getWalletId(int userId, int currencyId);
	
	public double getWalletABalance(int walletId);
	
	public double getWalletRBalance(int walletId);
	
	public boolean ifEnoughMoney(int walletId, double amountForCheck);
	
	public int getCurrencyId(int walletId);
	
	public String getCurrencyName(int currencyId);
}
=======
	List<Wallet> getAllWallets(int userId);
	
	List<Currency> getCurrencyList();
	
	int getWalletId(int userId, int currencyId);
	
	double getWalletABalance(int walletId);
	
	double getWalletRBalance(int walletId);

	boolean setWalletABalance(int walletId,double amount);

	int getCurrencyId(int walletId);

	String getCurrencyName(int currencyId);

	boolean setWalletRBalance(int walletId, double amount);

	boolean ifEnoughMoney(int walletId, double amountForCheck);
}
>>>>>>> 04262353b47fdd14c36825d96fcecbda53d964c1
