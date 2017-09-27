package me.exrates.model.dto;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.Getter;
import me.exrates.model.StockExchange;
import me.exrates.model.StockExchangeStats;
import me.exrates.model.util.BigDecimalProcessing;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Locale;

@Getter
public class StockExrateJsonNode {

    private final JsonNode jsonNode;
    private final String priceLastName;
    private final String priceBuyName;
    private final String priceSellName;
    private final String priceLowName;
    private final String priceHighName;
    private final String volumeName;

    public StockExrateJsonNode(JsonNode jsonNode, String priceLastName, String priceBuyName, String priceSellName, String priceLowName, String priceHighName, String volumeName) {
        this.jsonNode = jsonNode;
        this.priceLastName = priceLastName;
        this.priceBuyName = priceBuyName;
        this.priceSellName = priceSellName;
        this.priceLowName = priceLowName;
        this.priceHighName = priceHighName;
        this.volumeName = volumeName;
    }

    public StockExchangeStats extractStatsFromNode() {
        StockExchangeStats stockExchangeStats = new StockExchangeStats();
        BigDecimal priceLast = BigDecimalProcessing.parseLocale(jsonNode.get(priceLastName).asText(),
                Locale.ENGLISH, false);
        BigDecimal priceBuy = BigDecimalProcessing.parseLocale(jsonNode.get(priceBuyName).asText(),
                Locale.ENGLISH, false);
        BigDecimal priceSell = BigDecimalProcessing.parseLocale(jsonNode.get(priceSellName).asText(),
                Locale.ENGLISH, false);
        BigDecimal priceLow = BigDecimalProcessing.parseLocale(jsonNode.get(priceLowName).asText(),
                Locale.ENGLISH, false);
        BigDecimal priceHigh = BigDecimalProcessing.parseLocale(jsonNode.get(priceHighName).asText(),
                Locale.ENGLISH, false);
        BigDecimal volume = BigDecimalProcessing.parseLocale(jsonNode.get(volumeName).asText(),
                Locale.ENGLISH, false);

        stockExchangeStats.setPriceLast(priceLast);
        stockExchangeStats.setPriceBuy(priceBuy);
        stockExchangeStats.setPriceSell(priceSell);
        stockExchangeStats.setPriceLow(priceLow);
        stockExchangeStats.setPriceHigh(priceHigh);
        stockExchangeStats.setVolume(volume);
        stockExchangeStats.setDate(LocalDateTime.now());
        return stockExchangeStats;
    }
}
