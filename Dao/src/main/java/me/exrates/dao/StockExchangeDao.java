package me.exrates.dao;

import me.exrates.model.StockExchangeRate;

/**
 * Created by OLEG on 14.12.2016.
 */
public interface StockExchangeDao {
    void saveStockExchangeRate(StockExchangeRate stockExchangeRate);
}
