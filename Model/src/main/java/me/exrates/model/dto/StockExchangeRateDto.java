package me.exrates.model.dto;


import com.fasterxml.jackson.annotation.JsonProperty;
import me.exrates.model.StockExchangeStats;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by OLEG on 15.12.2016.
 */
public class StockExchangeRateDto {
    @JsonProperty(value = "currencyPair")
    private String currencyPairName;
    @JsonProperty(value = "exchangeStats")
    private Map<String, StockExchangeStats> exchangeStats = new HashMap<>();

    public String getCurrencyPairName() {
        return currencyPairName;
    }

    public void setCurrencyPairName(String currencyPairName) {
        this.currencyPairName = currencyPairName;
    }

    public Map<String, StockExchangeStats> getExchangeStats() {
        return exchangeStats;
    }

    public void setExchangeStats(Map<String, StockExchangeStats> exchangeStats) {
        this.exchangeStats = exchangeStats;
    }

    @Override
    public String toString() {
        return "StockExchangeRateDto{" +
                "currencyPairName='" + currencyPairName + '\'' +
                ", exchangeStats=" + exchangeStats +
                '}';
    }
}
