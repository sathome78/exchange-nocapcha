package me.exrates.service.stockExratesRetrieval;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import me.exrates.model.CurrencyPair;
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
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Created by OLEG on 20.12.2016.
 */
@Service
public class AlcurExRetrievalService implements StockExrateRetrievalService {
    private static final Logger LOGGER = LogManager.getLogger(AlcurExRetrievalService.class);
    private ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public List<StockExchangeStats> retrieveStats(StockExchange stockExchange) {
        List<StockExchangeStats> stockExchangeStatsList = new ArrayList<>();
        Map<String, CurrencyPair> currencyPairs = stockExchange.getAvailableCurrencyPairs().stream()
                .collect(Collectors.toMap(currencyPair -> currencyPair.getName().replace('/', '_'),
                        currencyPair -> currencyPair));

        String url = "https://alcurex.com/api/tickerapi";
        String jsonResponse = OkHttpUtils.sendGetRequest(url);
        try {
            JsonNode root = objectMapper.readTree(jsonResponse);
            currencyPairs.keySet().forEach(currencyPairName -> {
                JsonNode currencyPairNode = root.get(currencyPairName);
                if (currencyPairNode != null) {
                    StockExchangeStats stockExchangeStats = new StockExchangeStats();
                    stockExchangeStats.setStockExchange(stockExchange);
                    stockExchangeStats.setCurrencyPairId(currencyPairs.get(currencyPairName).getId());
                    BigDecimal priceLast = BigDecimalProcessing.parseLocale(currencyPairNode.get("last").asText(),
                            Locale.ENGLISH, false);
                    BigDecimal priceBuy = BigDecimalProcessing.parseLocale(currencyPairNode.get("highestBid").asText(),
                            Locale.ENGLISH, false);
                    BigDecimal priceSell = BigDecimalProcessing.parseLocale(currencyPairNode.get("lowestAsk").asText(),
                            Locale.ENGLISH, false);
                    BigDecimal priceLow = BigDecimalProcessing.parseLocale(currencyPairNode.get("low24hr").asText(),
                            Locale.ENGLISH, false);
                    BigDecimal priceHigh = BigDecimalProcessing.parseLocale(currencyPairNode.get("high24hr").asText(),
                            Locale.ENGLISH, false);
                    BigDecimal volume = BigDecimalProcessing.parseLocale(currencyPairNode.get("baseVolume").asText(),
                            Locale.ENGLISH, false);

                    stockExchangeStats.setPriceLast(priceLast);
                    stockExchangeStats.setPriceBuy(priceBuy);
                    stockExchangeStats.setPriceSell(priceSell);
                    stockExchangeStats.setPriceLow(priceLow);
                    stockExchangeStats.setPriceHigh(priceHigh);
                    stockExchangeStats.setVolume(volume);
                    stockExchangeStats.setDate(LocalDateTime.now());
                    stockExchangeStatsList.add(stockExchangeStats);
                }
            });
        } catch (IOException e) {
            LOGGER.error(e);
        }
        return stockExchangeStatsList;

    }

    @Override
    public String getStockExchangeName() {
        return "alcurEX";
    }

}
