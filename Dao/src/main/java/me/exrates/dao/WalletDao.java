package me.exrates.dao;

<<<<<<< HEAD
import java.util.List;

import me.exrates.model.Order;
import me.exrates.model.Wallet;

public interface WalletDao {

	public double getWalletABalance(int walletId);
	
	public double getWalletRBalance(int walletId);
	
	public int getWalletId(int userId, int currencyId);
	
	public boolean createNewWallet(Wallet wallet);
	
	public List<Wallet> getAllWallets(int userId);
	
}
=======
import me.exrates.model.Wallet;

import java.util.List;

public interface WalletDao {

	double getWalletABalance(int walletId);

	double getWalletRBalance(int walletId);

	boolean setWalletABalance(int walletId, double newBalance);

	boolean setWalletRBalance(int walletId, double newBalance);

	int getWalletId(int userId, int currencyId);

	boolean createNewWallet(Wallet wallet);

	List<Wallet> getAllWallets(int userId);
}
>>>>>>> 04262353b47fdd14c36825d96fcecbda53d964c1
