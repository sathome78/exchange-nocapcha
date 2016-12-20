package me.exrates.service.stockExratesRetrieval;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import me.exrates.dao.StockExchangeDao;
import me.exrates.model.CurrencyPair;
import me.exrates.model.StockExchange;
import me.exrates.model.StockExchangeStats;
import me.exrates.service.util.OkHttpUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Created by OLEG on 20.12.2016.
 */
/*@Service*/
public class BtceRetrievalService implements StockExrateRetrievalService {

    private static final Logger LOGGER = LogManager.getLogger(BtceRetrievalService.class);
    private ObjectMapper objectMapper = new ObjectMapper();

    /*@Autowired*/
    private StockExchangeDao stockExchangeDao;

    @Override
    public void retrieveAndSave(StockExchange stockExchange) {
        List<StockExchangeStats> stockExchangeStatsList = new ArrayList<>();
        Map<String, CurrencyPair> currencyPairs = stockExchange.getAvailableCurrencyPairs().stream()
                .collect(Collectors.toMap(currencyPair -> (convertCurrencyName(currencyPair.getCurrency1().getName()) + "_" +
                                convertCurrencyName(currencyPair.getCurrency2().getName())),
                        currencyPair -> currencyPair));

        String currencyPairsListString = currencyPairs.keySet().stream()
                .collect(Collectors.joining("-"));
        LOGGER.debug(currencyPairsListString);
        String url = "https://btc-e.com/api/3/ticker/" + currencyPairsListString;
        Map<String, String> params = Collections.singletonMap("ignore_invalid", "1");
        String jsonResponse = OkHttpUtils.sendGetRequest(url, params);
        try {
            JsonNode root = objectMapper.readTree(jsonResponse);
            currencyPairs.keySet().forEach(currencyPairName -> {
                JsonNode currencyPairNode = root.get(currencyPairName);
                if (currencyPairNode != null) {
                    StockExchangeStats stockExchangeStats = new StockExchangeStats();
                    stockExchangeStats.setStockExchangeId(stockExchange.getId());
                    stockExchangeStats.setCurrencyPairId(currencyPairs.get(currencyPairName).getId());
                    stockExchangeStats.setPriceBuy(currencyPairNode.get("buy").decimalValue());
                    stockExchangeStats.setPriceSell(currencyPairNode.get("sell").decimalValue());
                    stockExchangeStats.setPriceLow(currencyPairNode.get("low").decimalValue());
                    stockExchangeStats.setPriceHigh(currencyPairNode.get("high").decimalValue());
                    stockExchangeStats.setVolume(currencyPairNode.get("vol").decimalValue());
                    stockExchangeStats.setDate(LocalDateTime.now());
                    stockExchangeStatsList.add(stockExchangeStats);
                }
            });
        } catch (IOException e) {
            LOGGER.error(e);
        }
        stockExchangeDao.saveStockExchangeStatsList(stockExchangeStatsList);

    }

    @Override
    public String getStockExchangeName() {
        return "BTC-E";
    }

    private String convertCurrencyName(String currencyName) {
        return "RUB".equals(currencyName) ? "rur" : currencyName.toLowerCase();
    }
}
