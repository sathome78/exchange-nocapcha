package me.exrates.dao;

import java.util.List;

import me.exrates.model.Order;
import me.exrates.model.User;
import me.exrates.model.Wallet;

public interface WalletDao {

	double getWalletABalance(int walletId);

	double getWalletRBalance(int walletId);

	boolean setWalletABalance(int walletId, double newBalance);

	boolean setWalletRBalance(int walletId, double newBalance);

	int getWalletId(int userId, int currencyId);

	int createNewWallet(Wallet wallet);

	int getUserIdFromWallet(int walletId);

	List<Wallet> findAllByUser(int userId);

	Wallet findByUserAndCurrency(int userId,int currencyId);

	Wallet createWallet(int userId,int currencyId);
}