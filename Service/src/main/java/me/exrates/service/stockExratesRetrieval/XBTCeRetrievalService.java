package me.exrates.service.stockExratesRetrieval;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.log4j.Log4j2;
import me.exrates.model.CurrencyPair;
import me.exrates.model.StockExchange;
import me.exrates.model.StockExchangeStats;
import me.exrates.service.util.OkHttpUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Created by OLEG on 15.12.2016.
 */
@Log4j2(topic = "tracker")
@Service(value = "xBTCe")
public class XBTCeRetrievalService implements StockExrateRetrievalService {

    private ObjectMapper objectMapper = new ObjectMapper();

    @Override
    @Transactional(rollbackFor = Exception.class)
    public List<StockExchangeStats> retrieveStats(StockExchange stockExchange) {
        Map<String, CurrencyPair> currencyPairs = stockExchange.getAvailableCurrencyPairs().stream()
                .collect(Collectors.toMap(currencyPair -> (currencyPair.getCurrency1().getName() + currencyPair.getCurrency2().getName()),
                        currencyPair -> currencyPair));
        String urlBase = "https://cryptottlivewebapi.xbtce.net:8443/api/v1/public/ticker/";
        String urlFilter = currencyPairs.keySet().stream().collect(Collectors.joining(" "));
        String jsonResponse = OkHttpUtils.sendGetRequest(urlBase + urlFilter);
        List<StockExchangeStats> stockExchangeStatsList = new ArrayList<>();
        try {
            JsonNode root = objectMapper.readTree(jsonResponse);
            root.elements().forEachRemaining(jsonNode -> {
                StockExchangeStats stockExchangeStats = new StockExchangeStats();
                stockExchangeStats.setStockExchange(stockExchange);
                stockExchangeStats.setCurrencyPairId(currencyPairs.get(jsonNode.get("Symbol").asText()).getId());
                stockExchangeStats.setPriceLast(extractLastPrice(jsonNode));
                stockExchangeStats.setPriceBuy(jsonNode.get("LastBuyPrice").decimalValue());
                stockExchangeStats.setPriceSell(jsonNode.get("LastSellPrice").decimalValue());
                stockExchangeStats.setPriceLow(jsonNode.get("DailyBestBuyPrice").decimalValue());
                stockExchangeStats.setPriceHigh(jsonNode.get("DailyBestSellPrice").decimalValue());
                stockExchangeStats.setVolume(jsonNode.get("DailyTradedTotalVolume").decimalValue());
                stockExchangeStats.setDate(LocalDateTime.now());
                stockExchangeStatsList.add(stockExchangeStats);
            });


        } catch (IOException e) {
            log.error(e);
        }
        return stockExchangeStatsList;


    }

    private BigDecimal extractLastPrice(JsonNode jsonNode) {
        long lastBuyTimestamp = jsonNode.get("LastBuyTimestamp").longValue();
        long lastSellTimestamp = jsonNode.get("LastSellTimestamp").longValue();
        String fieldToExtract = lastBuyTimestamp > lastSellTimestamp ? "LastBuyPrice" : "LastSellPrice";
        return jsonNode.get(fieldToExtract).decimalValue();
    }


}
