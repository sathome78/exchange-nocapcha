package me.exrates.service.impl;

import me.exrates.dao.CurrencyDao;
import me.exrates.model.Currency;
import me.exrates.model.CurrencyPair;
import me.exrates.service.CurrencyService;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author Denis Savin (pilgrimm333@gmail.com)
 */
@Service
public class CurrencyServiceImpl implements CurrencyService {

    @Autowired
    private CurrencyDao currencyDao;

    private static final Logger logger = LogManager.getLogger(CurrencyServiceImpl.class);

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
    public CurrencyPair getCurrencyPairById(int currency1Id, int currency2Id) {
        logger.info("Begin 'getCurrencyPairById' method");
        return currencyDao.getCurrencyPairById(currency1Id, currency2Id);
    }
}