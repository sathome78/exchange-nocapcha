package me.exrates.service;

import java.util.List;

import me.exrates.model.Currency;
import me.exrates.model.User;
import me.exrates.model.Wallet;

public interface WalletService {

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

	int createNewWallet(Wallet wallet);

	int getUserIdFromWallet(int walletId);

	Wallet findByUserAndCurrency(User user, Currency currency);

	Wallet createWallet(User user,Currency currency);
}