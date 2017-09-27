package me.exrates.service.impl;

import lombok.extern.log4j.Log4j2;
import me.exrates.dao.StockExchangeDao;
import me.exrates.model.StockExchange;
import me.exrates.model.StockExchangeStats;
import me.exrates.service.StockExchangeService;
import me.exrates.service.stockExratesRetrieval.StockExrateRetrievalService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Created by OLEG on 14.12.2016.
 */
@Log4j2(topic = "tracker")
@Service
public class StockExchangeServiceImpl implements StockExchangeService {


    @Autowired
    private Map<String, StockExrateRetrievalService> stockExrateRetrievalServices;

    @Autowired
    private StockExchangeDao stockExchangeDao;


    @Override
 //   @Scheduled(cron = "0 0 * * * *")
    public void retrieveCurrencies() {
        log.debug("Start retrieving stock exchange statistics at: " + LocalDateTime.now());
        List<StockExchangeStats> stockExchangeStatsList = new ArrayList<>();

        stockExchangeDao.findAllActive().forEach(stockExchange -> {
            try {
                stockExchangeStatsList.addAll(stockExrateRetrievalServices.get(stockExchange.getName()).retrieveStats(stockExchange));
            } catch (Exception e) {
                log.warn(e.getMessage());
            }
        });

        stockExchangeDao.saveStockExchangeStatsList(stockExchangeStatsList);
    }

    @Override
    public List<StockExchangeStats> getStockExchangeStatistics(Integer currencyPairId) {
        return stockExchangeDao.getStockExchangeStatistics(currencyPairId);
    }
}
