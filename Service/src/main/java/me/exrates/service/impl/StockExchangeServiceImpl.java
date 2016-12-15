package me.exrates.service.impl;

import me.exrates.model.CurrencyPair;
import me.exrates.service.CurrencyService;
import me.exrates.service.StockExchangeService;
import me.exrates.service.stockExratesRetrieval.StockExrateRetrievalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.Collections;
import java.util.List;

/**
 * Created by OLEG on 14.12.2016.
 */
@Service
public class StockExchangeServiceImpl implements StockExchangeService{
    @Autowired
    private List<StockExrateRetrievalService> stockExrateRetrievalServices;

    @Autowired
    private CurrencyService currencyService;


    @Override
    @Scheduled(initialDelay = 5 * 1000L, fixedDelay = 1000 * 10000000000L)
    public void retrieveCurrencies() {
        CurrencyPair currencyPair = currencyService.getAllCurrencyPairs().stream().filter(pair -> "BTC/USD".equals(pair.getName())).findFirst().get();
        stockExrateRetrievalServices.forEach(service -> service.retrieveAndSave(Collections.singletonList(currencyPair)));
    }
}
