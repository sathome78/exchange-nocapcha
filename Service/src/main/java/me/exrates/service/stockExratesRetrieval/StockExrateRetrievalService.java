package me.exrates.service.stockExratesRetrieval;

import me.exrates.model.StockExchange;
import me.exrates.model.StockExchangeStats;

import java.util.List;

/**
 * Created by OLEG on 14.12.2016.
 */
public interface StockExrateRetrievalService {
    List<StockExchangeStats> retrieveStats(StockExchange stockExchange);
}
