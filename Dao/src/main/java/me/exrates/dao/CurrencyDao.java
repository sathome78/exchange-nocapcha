package me.exrates.dao;

import java.util.List;

import me.exrates.model.Currency;

public interface CurrencyDao {

	public List<Currency> getCurrList();
	
	public int getCurrencyId(int walletId);
	
	public String getCurrencyName(int currencyId);
}
