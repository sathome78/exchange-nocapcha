package me.exrates.dao;

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
