package me.exrates.service;

import me.exrates.model.Currency;
import me.exrates.model.CurrencyPair;

import java.math.BigDecimal;
import java.util.List;

/**
 * @author Denis Savin (pilgrimm333@gmail.com)
 */
public interface CurrencyService {

    String getCurrencyName(int currencyId);

    List<Currency> getAllCurrencies();

    Currency findByName(String name);

    Currency findById(int id);

    List<CurrencyPair> getAllCurrencyPairs();

    CurrencyPair getCurrencyPairById(int currency1Id, int currency2Id);

    String amountToString(BigDecimal amount, String currency);

    int resolvePrecision(String currency);
}
