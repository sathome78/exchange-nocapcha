package me.exrates.services;

import java.util.List;

import me.exrates.beans.Currency;
import me.exrates.beans.Wallet;

public interface WalletService {

	public List<Wallet> getAllWallets(int userId); 
	
	public List<Currency> getCurrencyList();
}
