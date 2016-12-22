package me.exrates.service.impl;

import me.exrates.dao.StockExchangeDao;
import me.exrates.model.StockExchange;
import me.exrates.model.StockExchangeStats;
import me.exrates.model.dto.StockExchangeRateDto;
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
@Service
public class StockExchangeServiceImpl implements StockExchangeService {

    private static final Logger LOGGER = LogManager.getLogger("mobileAPI");

    @Autowired
    private List<StockExrateRetrievalService> stockExrateRetrievalServices;

    @Autowired
    private StockExchangeDao stockExchangeDao;


    @Override
    @Scheduled(cron = "0 23 * * *")
    public void retrieveCurrencies() {
        LOGGER.debug("Start retrieving stock exchange statistics at: " + LocalDateTime.now());
        Map<String, StockExchange> stockExchanges = stockExchangeDao.findAll().stream()
                .collect(Collectors.toMap(StockExchange::getName, stockExchange -> stockExchange));
        LOGGER.debug(stockExchanges);
        List<StockExchangeStats> stockExchangeStatsList = new ArrayList<>();

        stockExrateRetrievalServices.forEach(service -> {
            stockExchangeStatsList.addAll(service.retrieveStats(stockExchanges.get(service.getStockExchangeName())));
        });
        stockExchangeDao.saveStockExchangeStatsList(stockExchangeStatsList);
    }

    @Override
    public List<StockExchangeRateDto> getStockExchangeStatistics(List<Integer> currencyPairIds) {
        return stockExchangeDao.getStockExchangeStatistics(currencyPairIds);
    }
}
