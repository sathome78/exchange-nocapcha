package me.exrates.service;

import me.exrates.model.Currency;
import me.exrates.model.User;
import me.exrates.model.Wallet;

import java.math.BigDecimal;
import java.util.List;

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

	Wallet create(User user, Currency currency);

	void depositActiveBalance(Wallet wallet, BigDecimal sum);

	void withdrawActiveBalance(Wallet wallet, BigDecimal sum);

	void depositReservedBalance(Wallet wallet, BigDecimal sum);

	void withdrawReservedBalance(Wallet wallet,BigDecimal sum);
}