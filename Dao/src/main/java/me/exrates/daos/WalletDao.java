package me.exrates.daos;

import java.util.List;

import me.exrates.beans.Wallet;

public interface WalletDao {

	public double getWalletABalance(Wallet wallet);
	
	public double getWalletRBalance(Wallet wallet);
	
	public int getWalletId(int userId, int currencyId);
	
	public boolean createNewWallet(Wallet wallet);
	
	public List<Wallet> getAllWallets(int userId);
	
}
