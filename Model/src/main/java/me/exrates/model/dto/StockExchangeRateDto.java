package me.exrates.model.dto;


import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by OLEG on 15.12.2016.
 */
public class StockExchangeRateDto {
    @JsonProperty(value = "currencyPair")
    private String currencyPairName;
    @JsonProperty(value = "exchangeRates")
    private Map<String, BigDecimal> rates = new HashMap<>();

    public String getCurrencyPairName() {
        return currencyPairName;
    }

    public void setCurrencyPairName(String currencyPairName) {
        this.currencyPairName = currencyPairName;
    }

    public Map<String, BigDecimal> getRates() {
        return rates;
    }

    public void setRates(Map<String, BigDecimal> rates) {
        this.rates = rates;
    }

    @Override
    public String toString() {
        return "StockExchangeRateDto{" +
                "currencyPairName='" + currencyPairName + '\'' +
                ", rates=" + rates +
                '}';
    }
}
