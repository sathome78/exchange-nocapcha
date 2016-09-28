package me.exrates.dao;

import me.exrates.model.Currency;
import me.exrates.model.CurrencyPair;

import java.math.BigDecimal;
import java.util.List;

public interface CurrencyDao {

	List<Currency> getCurrList();

	int getCurrencyId(int walletId);

	String getCurrencyName(int currencyId);

	Currency findByName(String name);

	Currency findById(int id);

	List<Currency> findAllCurrencies();

    boolean updateMinWithdraw(int currencyId, BigDecimal minAmount);

    List<CurrencyPair> getAllCurrencyPairs();

	CurrencyPair getCurrencyPairById(int currency1Id, int currency2Id);

	CurrencyPair findCurrencyPairById(int currencyPairId);
}