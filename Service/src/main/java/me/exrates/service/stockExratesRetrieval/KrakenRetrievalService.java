package me.exrates.service.stockExratesRetrieval;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import me.exrates.dao.StockExchangeDao;
import me.exrates.model.StockExchange;
import me.exrates.model.StockExchangeRate;
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
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * Created by OLEG on 14.12.2016.
 */
@Service
public class KrakenRetrievalService implements StockExrateRetrievalService {

    private static final Logger LOGGER = LogManager.getLogger(KrakenRetrievalService.class);
    private ObjectMapper objectMapper = new ObjectMapper();
    private Map<String, String> altCurrencyNames = new HashMap<String, String>() {{
        put("BTC", "XXBT");
        put("USD", "ZUSD");
        put("EUR", "ZEUR");
    }};


    private final String STOCK_EXCHANGE_NAME = "Kraken";

    @Autowired
    private StockExchangeDao stockExchangeDao;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void retrieveAndSave(StockExchange stockExchange) {
        stockExchange.getAvailableCurrencyPairs().forEach(currencyPair -> {
            String name = altCurrencyNames.get(currencyPair.getCurrency1().getName()) + altCurrencyNames.get(currencyPair.getCurrency2().getName());
            String url = "https://api.kraken.com/0/public/Ticker";
            Map<String, String> params = Collections.singletonMap("pair", name);
            String jsonResponse = OkHttpUtils.sendGetRequest(url, params);
            LOGGER.debug(jsonResponse);
            try {
                JsonNode root = objectMapper.readTree(jsonResponse);
                StockExchangeRate stockExchangeRate = new StockExchangeRate();
                stockExchangeRate.setCurrencyPairId(currencyPair.getId());
                String priceString = root.get("result").get(name).get("c").get(0).asText();
                LOGGER.debug(priceString);
                BigDecimal exrate = BigDecimalProcessing.parseLocale(priceString, Locale.ENGLISH, true);
                stockExchangeRate.setDate(LocalDateTime.now());
                stockExchangeRate.setExrate(exrate);
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
