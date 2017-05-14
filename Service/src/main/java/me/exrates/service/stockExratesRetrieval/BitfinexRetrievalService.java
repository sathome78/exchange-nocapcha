package me.exrates.service.stockExratesRetrieval;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import me.exrates.model.StockExchange;
import me.exrates.model.StockExchangeStats;
import me.exrates.model.util.BigDecimalProcessing;
import me.exrates.service.util.OkHttpUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Created by OLEG on 14.12.2016.
 */
@Service
public class BitfinexRetrievalService implements StockExrateRetrievalService {

    private static final Logger LOGGER = LogManager.getLogger(BitfinexRetrievalService.class);
    private ObjectMapper objectMapper = new ObjectMapper();

    private final String STOCK_EXCHANGE_NAME = "BITFINEX";


    @Override
    @Transactional(rollbackFor = Exception.class)
    public List<StockExchangeStats> retrieveStats(StockExchange stockExchange) {
        List<StockExchangeStats> stockExchangeStatsList = new ArrayList<>();


        stockExchange.getAvailableCurrencyPairs().forEach(currencyPair -> {
            String name = currencyPair.getCurrency1().getName().toLowerCase() + currencyPair.getCurrency2().getName().toLowerCase();
            String url = "https://api.bitfinex.com/v1/pubticker/" + name;
            String jsonResponse = OkHttpUtils.sendGetRequest(url);
            try {
                JsonNode root = objectMapper.readTree(jsonResponse);
                StockExchangeStats stockExchangeStats = new StockExchangeStats();
                stockExchangeStats.setCurrencyPairId(currencyPair.getId());
                BigDecimal priceLast = BigDecimalProcessing.parseLocale(root.get("last_price").asText(), Locale.ENGLISH, false);
                BigDecimal priceBuy = BigDecimalProcessing.parseLocale(root.get("bid").asText(), Locale.ENGLISH, false);
                BigDecimal priceSell = BigDecimalProcessing.parseLocale(root.get("ask").asText(), Locale.ENGLISH, false);
                BigDecimal priceLow = BigDecimalProcessing.parseLocale(root.get("low").asText(), Locale.ENGLISH, false);
                BigDecimal priceHigh = BigDecimalProcessing.parseLocale(root.get("high").asText(), Locale.ENGLISH, false);
                BigDecimal volume = BigDecimalProcessing.parseLocale(root.get("volume").asText(), Locale.ENGLISH, false);
                stockExchangeStats.setDate(LocalDateTime.now());
                stockExchangeStats.setPriceLast(priceLast);
                stockExchangeStats.setStockExchange(stockExchange);
                stockExchangeStats.setPriceBuy(priceBuy);
                stockExchangeStats.setPriceSell(priceSell);
                stockExchangeStats.setPriceLow(priceLow);
                stockExchangeStats.setPriceHigh(priceHigh);
                stockExchangeStats.setVolume(volume);
                stockExchangeStatsList.add(stockExchangeStats);
            } catch (IOException e) {
                LOGGER.error(e);
            }

        });
        return stockExchangeStatsList;

    }

    @Override
    public String getStockExchangeName() {
        return STOCK_EXCHANGE_NAME;
    }
}
