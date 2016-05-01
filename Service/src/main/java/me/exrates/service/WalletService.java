package me.exrates.service;

import me.exrates.model.Currency;
import me.exrates.model.User;
import me.exrates.model.Wallet;
import me.exrates.model.dto.UsersWalletsDto;
import me.exrates.model.dto.UsersWalletsSummaryDto;

import java.math.BigDecimal;
import java.util.List;

public interface WalletService {

	void balanceRepresentation (Wallet wallet);

	List<Wallet> getAllWallets(int userId);

	List<Currency> getCurrencyList();

	int getWalletId(int userId, int currencyId);

	BigDecimal getWalletABalance(int walletId);

	BigDecimal getWalletRBalance(int walletId);

	boolean setWalletABalance(int walletId,BigDecimal amount);

	int getCurrencyId(int walletId);

	String getCurrencyName(int currencyId);

	boolean setWalletRBalance(int walletId, BigDecimal amount);

	boolean ifEnoughMoney(int walletId, BigDecimal amountForCheck);

	int createNewWallet(Wallet wallet);

	int getUserIdFromWallet(int walletId);

	Wallet findByUserAndCurrency(User user, Currency currency);

	Wallet create(User user, Currency currency);

	void depositActiveBalance(Wallet wallet, BigDecimal sum);

	void withdrawActiveBalance(Wallet wallet, BigDecimal sum);

	void depositReservedBalance(Wallet wallet, BigDecimal sum);

	void withdrawReservedBalance(Wallet wallet,BigDecimal sum);

	List<UsersWalletsSummaryDto> getUsersWalletsSummary();

	List<UsersWalletsDto> getUsersWalletsList();
}