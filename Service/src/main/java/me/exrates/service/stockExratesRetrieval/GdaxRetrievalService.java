package me.exrates.service.stockExratesRetrieval;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import me.exrates.dao.StockExchangeDao;
import me.exrates.model.CurrencyPair;
import me.exrates.model.StockExchange;
import me.exrates.model.StockExchangeRate;
import me.exrates.model.util.BigDecimalProcessing;
import me.exrates.service.util.OkHttpUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

/**
 * Created by OLEG on 14.12.2016.
 */
@Service
public class GdaxRetrievalService implements StockExrateRetrievalService {

    private static final Logger LOGGER = LogManager.getLogger(GdaxRetrievalService.class);
    private ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    private StockExchangeDao stockExchangeDao;

    @Override
    public void retrieveAndSave(List<CurrencyPair> currencyPairs) {
        StockExchange stockExchange = stockExchangeDao.getStockExchangeByName("GDAX");
        currencyPairs.forEach(currencyPair -> {
            String name = currencyPair.getName().replace('/', '-');
            String url = "https://api.gdax.com/products/" + name + "/ticker";
            String jsonResponse = OkHttpUtils.sendGetRequest(url, Collections.EMPTY_MAP);
            LOGGER.debug(jsonResponse);
            try {
                JsonNode root = objectMapper.readTree(jsonResponse);
                StockExchangeRate stockExchangeRate = new StockExchangeRate();

                stockExchangeRate.setCurrencyPair(currencyPair);
                LOGGER.debug(root.get("price"));
                BigDecimal exrate = BigDecimalProcessing.parseLocale(root.get("price").textValue(), Locale.ENGLISH, true);
                stockExchangeRate.setDate(LocalDateTime.now());
                stockExchangeRate.setExrate(exrate);
                stockExchangeRate.setStockExchange(stockExchange);
                stockExchangeDao.saveStockExchangeRate(stockExchangeRate);
            } catch (IOException e) {
                LOGGER.error(e);
            }

        });
    }
}
