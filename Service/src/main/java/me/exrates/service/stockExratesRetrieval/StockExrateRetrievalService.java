package me.exrates.service.stockExratesRetrieval;

import me.exrates.model.CurrencyPair;
import me.exrates.model.StockExchange;

import java.util.List;

/**
 * Created by OLEG on 14.12.2016.
 */
public interface StockExrateRetrievalService {
    void retrieveAndSave(StockExchange stockExchange);

    String getStockExchangeName();
}
