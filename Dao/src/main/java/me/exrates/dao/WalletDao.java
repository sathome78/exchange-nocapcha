package me.exrates.dao;

import java.util.List;

import me.exrates.model.Wallet;

public interface WalletDao {

	double getWalletABalance(int walletId);

	double getWalletRBalance(int walletId);

	double setWalletABalance(int walletId);

	double setWalletRBalance(int walletId);

	int getWalletId(int userId, int currencyId);

	boolean createNewWallet(Wallet wallet);

	List<Wallet> getAllWallets(int userId);
}