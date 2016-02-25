package me.exrates.service;

import me.exrates.model.Currency;

import java.util.List;

/**
 * @author Denis Savin (pilgrimm333@gmail.com)
 */
public interface CurrencyService {

    List<Currency> getAllCurrencies();

    Currency findByName(String name);

    Currency findById(int id);
}