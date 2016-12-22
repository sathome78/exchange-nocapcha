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
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

/**
 * Created by OLEG on 20.12.2016.
 */
@Service
public class BitstampRetrievalService implements StockExrateRetrievalService {

    private static final Logger LOGGER = LogManager.getLogger(BitstampRetrievalService.class);
    private ObjectMapper objectMapper = new ObjectMapper();


    @Override
    public List<StockExchangeStats> retrieveStats(StockExchange stockExchange) {
        List<StockExchangeStats> stockExchangeStatsList = new ArrayList<>();
        stockExchange.getAvailableCurrencyPairs().forEach(currencyPair -> {
            String currencyPairName = currencyPair.getCurrency1().getName().toLowerCase() +
                    currencyPair.getCurrency2().getName().toLowerCase();
            String url = "https://www.bitstamp.net/api/v2/ticker/" + currencyPairName + "/";
            String jsonResponse = OkHttpUtils.sendGetRequest(url);
            try {
                JsonNode root = objectMapper.readTree(jsonResponse);
                StockExchangeStats stockExchangeStats = new StockExchangeStats();
                stockExchangeStats.setStockExchangeId(stockExchange.getId());
                stockExchangeStats.setCurrencyPairId(currencyPair.getId());
                BigDecimal priceBuy = BigDecimalProcessing.parseLocale(root.get("bid").asText(), Locale.ENGLISH, false);
                BigDecimal priceSell = BigDecimalProcessing.parseLocale(root.get("ask").asText(), Locale.ENGLISH, false);
                BigDecimal priceLow = BigDecimalProcessing.parseLocale(root.get("low").asText(), Locale.ENGLISH, false);
                BigDecimal priceHigh = BigDecimalProcessing.parseLocale(root.get("high").asText(), Locale.ENGLISH, false);
                BigDecimal volume = BigDecimalProcessing.parseLocale(root.get("volume").asText(), Locale.ENGLISH, false);
                stockExchangeStats.setPriceBuy(priceBuy);
                stockExchangeStats.setPriceSell(priceSell);
                stockExchangeStats.setPriceLow(priceLow);
                stockExchangeStats.setPriceHigh(priceHigh);
                stockExchangeStats.setVolume(volume);
                stockExchangeStats.setDate(LocalDateTime.now());
                stockExchangeStatsList.add(stockExchangeStats);
            } catch (IOException e) {
                LOGGER.error(e);
            }
        });
        return stockExchangeStatsList;


    }

    @Override
    public String getStockExchangeName() {
        return "Bitstamp";
    }
}
