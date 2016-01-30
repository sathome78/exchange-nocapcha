package me.exrates.dao;

import java.math.BigDecimal;
import java.util.List;

import me.exrates.model.Wallet;

public interface WalletDao {

	double getWalletABalance(int walletId);

	double getWalletRBalance(int walletId);

	boolean setWalletABalance(int walletId, BigDecimal newBalance);

	boolean setWalletRBalance(int walletId, BigDecimal newBalance);

	int getWalletId(int userId, int currencyId);

	boolean createNewWallet(Wallet wallet);

	List<Wallet> getAllWallets(int userId);
}