package me.exrates.service;

import me.exrates.model.StockExchangeStats;

import javax.annotation.PostConstruct;
import java.util.List;

/**
 * Created by OLEG on 14.12.2016.
 */
public interface StockExchangeService {
    @PostConstruct
    void retrieveCurrencies();

    List<StockExchangeStats> getStockExchangeStatistics(Integer currencyPairId);
}
