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
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * Kraken API Response Syntax:
 *      a = ask array(<price>, <whole lot volume>, <lot volume>),
 *      b = bid array(<price>, <whole lot volume>, <lot volume>),
 *      c = last trade closed array(<price>, <lot volume>),
 *      v = volume array(<today>, <last 24 hours>),
 *      p = volume weighted average price array(<today>, <last 24 hours>),
 *      t = number of trades array(<today>, <last 24 hours>),
 *      l = low array(<today>, <last 24 hours>),
 *      h = high array(<today>, <last 24 hours>),
 *      o = today's opening price
 *
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
    private final String ASK_ARRAY = "a";
    private final String BID_ARRAY = "b";
    private final String LOW_ARRAY = "l";
    private final String HIGH_ARRAY = "h";
    private final String VOLUME_ARRAY = "v";
    private final int ASK_PRICE_ITEM = 0;
    private final int BID_PRICE_ITEM = 0;
    private final int LOW_PRICE_ITEM = 0;
    private final int HIGH_PRICE_ITEM = 0;
    private final int VOLUME_ITEM = 0;

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
                StockExchangeStats stockExchangeStats= new StockExchangeStats();
                stockExchangeStats.setCurrencyPairId(currencyPair.getId());
                JsonNode currencyPairNode = root.get("result").get(name);
                BigDecimal priceBuy = BigDecimalProcessing.parseLocale(currencyPairNode.get(BID_ARRAY)
                        .get(BID_PRICE_ITEM).asText(), Locale.ENGLISH, false);
                BigDecimal priceSell = BigDecimalProcessing.parseLocale(currencyPairNode.get(ASK_ARRAY)
                        .get(ASK_PRICE_ITEM).asText(), Locale.ENGLISH, false);
                BigDecimal priceLow = BigDecimalProcessing.parseLocale(currencyPairNode.get(LOW_ARRAY)
                        .get(LOW_PRICE_ITEM).asText(), Locale.ENGLISH, false);
                BigDecimal priceHigh = BigDecimalProcessing.parseLocale(currencyPairNode.get(HIGH_ARRAY)
                        .get(HIGH_PRICE_ITEM).asText(), Locale.ENGLISH, false);
                BigDecimal volume = BigDecimalProcessing.parseLocale(currencyPairNode.get(VOLUME_ARRAY)
                        .get(VOLUME_ITEM).asText(), Locale.ENGLISH, false);

                stockExchangeStats.setDate(LocalDateTime.now());
                stockExchangeStats.setStockExchangeId(stockExchange.getId());
                stockExchangeStats.setPriceBuy(priceBuy);
                stockExchangeStats.setPriceSell(priceSell);
                stockExchangeStats.setPriceLow(priceLow);
                stockExchangeStats.setPriceHigh(priceHigh);
                stockExchangeStats.setVolume(volume);
                stockExchangeDao.saveStockExchangeStats(stockExchangeStats);
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
