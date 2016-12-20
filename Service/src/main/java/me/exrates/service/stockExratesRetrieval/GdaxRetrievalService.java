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
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Locale;

/**
 * Created by OLEG on 14.12.2016.
 */
/*
@Service
*/
public class GdaxRetrievalService implements StockExrateRetrievalService {

    private static final Logger LOGGER = LogManager.getLogger(GdaxRetrievalService.class);
    private ObjectMapper objectMapper = new ObjectMapper();

    private final String STOCK_EXCHANGE_NAME = "GDAX";

    /*@Autowired*/
    private StockExchangeDao stockExchangeDao;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void retrieveAndSave(StockExchange stockExchange) {
        stockExchange.getAvailableCurrencyPairs().forEach(currencyPair -> {
            String name = currencyPair.getName().replace('/', '-');
            String url = "https://api.gdax.com/products/" + name + "/ticker";
            String jsonResponse = OkHttpUtils.sendGetRequest(url, Collections.EMPTY_MAP);
            LOGGER.debug(jsonResponse);
            try {
                JsonNode root = objectMapper.readTree(jsonResponse);
                StockExchangeStats stockExchangeRate = new StockExchangeStats();

                stockExchangeRate.setCurrencyPairId(currencyPair.getId());
                LOGGER.debug(root.get("price"));
                BigDecimal exrate = BigDecimalProcessing.parseLocale(root.get("price").asText(), Locale.ENGLISH, true);
                stockExchangeRate.setDate(LocalDateTime.now());
                stockExchangeRate.setStockExchangeId(stockExchange.getId());
                stockExchangeDao.saveStockExchangeStats(stockExchangeRate);
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
