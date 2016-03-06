package me.exrates.dao;

import java.util.List;

import me.exrates.model.Currency;
import me.exrates.model.CurrencyPair;

public interface CurrencyDao {

	List<Currency> getCurrList();

	int getCurrencyId(int walletId);

	String getCurrencyName(int currencyId);

	Currency findByName(String name);

	Currency findById(int id);

	List<CurrencyPair> getAllCurrencyPairs();

	CurrencyPair getCurrencyPairById(int currency1Id, int currency2Id);
}