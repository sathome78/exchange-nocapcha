package me.exrates.service.impl;

import me.exrates.dao.CurrencyDao;
import me.exrates.model.Currency;
import me.exrates.model.CurrencyPair;
import me.exrates.service.CurrencyService;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static java.math.BigDecimal.ROUND_CEILING;

/**
 * @author Denis Savin (pilgrimm333@gmail.com)
 */
@Service
public class CurrencyServiceImpl implements CurrencyService {

    @Autowired
    private CurrencyDao currencyDao;

    private static final Logger logger = LogManager.getLogger(CurrencyServiceImpl.class);
    private static final Set<String> CRYPTO = new HashSet<String>() {
        {
            add("EDRC");
            add("BTC");
            add("LTC");
        }
    };
    private static final int CRYPTO_PRECISION = 9;
    private static final int DEFAULT_PRECISION = 2;

    @Override
    public String getCurrencyName(int currencyId) {
        logger.info("Begin 'getCurrencyName' method");
        return currencyDao.getCurrencyName(currencyId);
    }

    @Override
    public List<Currency> getAllCurrencies() {
        logger.info("Begin 'getAllCurrencies' method");
        return currencyDao.getCurrList();
    }

    @Override
    public Currency findByName(String name) {
        logger.info("Begin 'findByName' method");
        return currencyDao.findByName(name);
    }

    @Override
    public Currency findById(int id) {
        logger.info("Begin 'findById' method");
        return currencyDao.findById(id);
    }

    @Override
    public List<CurrencyPair> getAllCurrencyPairs() {
        logger.info("Begin 'getAllCurrencyPairs' method");
        return currencyDao.getAllCurrencyPairs();
    }

    @Override
    public CurrencyPair findCurrencyPairById(int currencyPairId) {
        logger.info("Begin 'findCurrencyPairById' method");
        return currencyDao.findCurrencyPairById(currencyPairId);
    }

    @Override
    public String amountToString(final BigDecimal amount, final String currency) {
        return amount.setScale(resolvePrecision(currency), ROUND_CEILING).toString();
    }

    @Override
    public int resolvePrecision(final String currency) {
        return CRYPTO.contains(currency) ? CRYPTO_PRECISION : DEFAULT_PRECISION;
    }

}