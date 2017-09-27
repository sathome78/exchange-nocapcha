package me.exrates.service.stockExratesRetrieval;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.log4j.Log4j2;
import me.exrates.model.StockExchange;
import me.exrates.model.StockExchangeStats;
import me.exrates.model.util.BigDecimalProcessing;
import me.exrates.service.util.OkHttpUtils;
import org.springframework.beans.factory.annotation.Autowired;
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
@Log4j2(topic = "tracker")
@Service(value = "BITFINEX")
public class BitfinexRetrievalService implements StockExrateRetrievalService {

    @Autowired
    private ObjectMapper objectMapper;

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
                log.error(e);
            }

        });
        return stockExchangeStatsList;

    }

}
