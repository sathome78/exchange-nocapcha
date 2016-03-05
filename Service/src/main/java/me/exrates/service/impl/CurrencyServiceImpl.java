package me.exrates.service.impl;

import me.exrates.dao.CurrencyDao;
import me.exrates.model.Currency;
import me.exrates.model.CurrencyPair;
import me.exrates.service.CurrencyService;
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

    @Override
    public String getCurrencyName(int currencyId) {
        return currencyDao.getCurrencyName(currencyId);
    }

    @Override
    public List<Currency> getAllCurrencies() {
        return currencyDao.getCurrList();
    }

    @Override
    public Currency findByName(String name) {
        return currencyDao.findByName(name);
    }

    @Override
    public Currency findById(int id) {
        return currencyDao.findById(id);
    }

    @Override
    public List<CurrencyPair> getAllCurrencyPairs() {
        return currencyDao.getAllCurrencyPairs();
    }

    @Override
    public CurrencyPair getCurrencyPairById(int currency1Id, int currency2Id) {
        return currencyDao.getCurrencyPairById(currency1Id, currency2Id);
    }
}