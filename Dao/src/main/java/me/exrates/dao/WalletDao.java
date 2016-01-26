package me.exrates.dao;

import java.util.List;

import me.exrates.model.Wallet;

public interface WalletDao {

	double getWalletABalance(int walletId);

	double getWalletRBalance(int walletId);

	boolean setWalletABalance(int walletId,double newBalance);

	boolean setWalletRBalance(int walletId,double newBalance);

	int getWalletId(int userId, int currencyId);

	boolean createNewWallet(Wallet wallet);

	List<Wallet> getAllWallets(int userId);
}