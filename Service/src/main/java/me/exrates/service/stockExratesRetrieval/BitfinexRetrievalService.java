package me.exrates.service.stockExratesRetrieval;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import me.exrates.dao.StockExchangeDao;
import me.exrates.model.StockExchange;
import me.exrates.model.StockExchangeStats;
import me.exrates.model.util.BigDecimalProcessing;
import me.exrates.service.util.OkHttpUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Locale;

/**
 * Created by OLEG on 14.12.2016.
 */
@Service
public class BitfinexRetrievalService implements StockExrateRetrievalService {

    private static final Logger LOGGER = LogManager.getLogger(BitfinexRetrievalService.class);
    private ObjectMapper objectMapper = new ObjectMapper();

    private final String STOCK_EXCHANGE_NAME = "BITFINEX";

    @Autowired
    private StockExchangeDao stockExchangeDao;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void retrieveAndSave(StockExchange stockExchange) {
        stockExchange.getAvailableCurrencyPairs().forEach(currencyPair -> {
            String name = currencyPair.getCurrency1().getName().toLowerCase() + currencyPair.getCurrency2().getName().toLowerCase();
            String url = "https://api.bitfinex.com/v1/pubticker/" + name;
            String jsonResponse = OkHttpUtils.sendGetRequest(url, Collections.EMPTY_MAP);
            try {
                JsonNode root = objectMapper.readTree(jsonResponse);
                StockExchangeStats stockExchangeRate = new StockExchangeStats();
                stockExchangeRate.setCurrencyPairId(currencyPair.getId());
                LOGGER.debug(root.get("mid"));
                BigDecimal exrate = BigDecimalProcessing.parseLocale(root.get("mid").asText(), Locale.ENGLISH, true);
                LOGGER.debug(exrate);
                stockExchangeRate.setDate(LocalDateTime.now());
                stockExchangeRate.setStockExchangeId(stockExchange.getId());
                stockExchangeDao.saveStockExchangeRate(stockExchangeRate);
            } catch (IOException e) {
                LOGGER.error(e);
            }

        });
    }

    @Override
    public String getStockExchangeName() {
        return STOCK_EXCHANGE_NAME;
    }
}
