package me.exrates.service.stockExratesRetrieval;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import me.exrates.model.CurrencyPair;
import me.exrates.model.StockExchange;
import me.exrates.model.StockExchangeStats;
import me.exrates.service.util.OkHttpUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Created by OLEG on 15.12.2016.
 */
/*@Service*/
public class XBTCeRetrievalService implements StockExrateRetrievalService {

    private static final Logger LOGGER = LogManager.getLogger(XBTCeRetrievalService.class);
    private ObjectMapper objectMapper = new ObjectMapper();
    private final String STOCK_EXCHANGE_NAME = "xBTCe";



    @Override
    @Transactional(rollbackFor = Exception.class)
    public List<StockExchangeStats> retrieveStats(StockExchange stockExchange) {
        Map<String, CurrencyPair> currencyPairs = stockExchange.getAvailableCurrencyPairs().stream()
                .collect(Collectors.toMap(currencyPair -> (currencyPair.getCurrency1().getName() + currencyPair.getCurrency2().getName()),
                        currencyPair -> currencyPair));
        String urlBase = "https://cryptottlivewebapi.xbtce.net:8443/api/v1/public/ticker/";
        String urlFilter = currencyPairs.keySet().stream().collect(Collectors.joining(" "));

        LOGGER.debug(urlFilter);
        String jsonResponse = OkHttpUtils.sendGetRequest(urlBase + urlFilter);
        List<StockExchangeStats> stockExchangeStatsList = new ArrayList<>();
        try {
            JsonNode root = objectMapper.readTree(jsonResponse);
            root.elements().forEachRemaining(jsonNode -> {
                StockExchangeStats stockExchangeStats = new StockExchangeStats();
                stockExchangeStats.setStockExchangeId(stockExchange.getId());
                stockExchangeStats.setCurrencyPairId(currencyPairs.get(jsonNode.get("Symbol").asText()).getId());
                stockExchangeStats.setPriceBuy(jsonNode.get("LastBuyPrice").decimalValue());
                stockExchangeStats.setPriceSell(jsonNode.get("LastSellPrice").decimalValue());
                stockExchangeStats.setPriceLow(jsonNode.get("DailyBestBuyPrice").decimalValue());
                stockExchangeStats.setPriceHigh(jsonNode.get("DailyBestSellPrice").decimalValue());
                stockExchangeStats.setVolume(jsonNode.get("DailyTradedTotalVolume").decimalValue());
                stockExchangeStats.setDate(LocalDateTime.now());
                stockExchangeStatsList.add(stockExchangeStats);
            });


        } catch (IOException e) {
            LOGGER.error(e);
        }
        return stockExchangeStatsList;


    }

    @Override
    public String getStockExchangeName() {
        return STOCK_EXCHANGE_NAME;
    }


}
