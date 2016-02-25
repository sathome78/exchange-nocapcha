package me.exrates.dao;

import java.util.List;

import me.exrates.model.Currency;

public interface CurrencyDao {

	List<Currency> getCurrList();

	int getCurrencyId(int walletId);

	String getCurrencyName(int currencyId);

	Currency findByName(String name);

	Currency findById(int id);
}