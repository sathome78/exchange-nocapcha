package me.exrates.service;

import me.exrates.model.Currency;
import me.exrates.model.CurrencyLimit;
import me.exrates.model.CurrencyPair;
import me.exrates.model.enums.OperationType;

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

    List<Currency> findAllCurrencies();

    boolean updateMinWithdraw(int currencyId, BigDecimal minAmount);

    void updateCurrencyLimit(int currencyId, OperationType operationType, String roleName, BigDecimal minAmount);

    List<CurrencyLimit> retrieveCurrencyLimitsFroRole(String roleName, OperationType operationType);

    List<CurrencyPair> getAllCurrencyPairs();

    CurrencyPair findCurrencyPairById(int currencyPairId);

    String amountToString(BigDecimal amount, String currency);

    int resolvePrecision(String currency);
}
