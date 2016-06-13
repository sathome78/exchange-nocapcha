package me.exrates.model.dto;

import me.exrates.model.CurrencyPair;

import java.math.BigDecimal;

/**
 * Created by Valk on 12.04.16.
 */
public class ExOrderStatisticsDto {
    private CurrencyPair currencyPair;
    private String firstOrderAmountBase;
    private String firstOrderRate;
    private String lastOrderAmountBase;
    private String lastOrderRate;
    private String minRate;
    private String maxRate;
    private String sumBase;
    private String sumConvert;

    /*constructors*/

    public ExOrderStatisticsDto() {
    }

    public ExOrderStatisticsDto(CurrencyPair currencyPair) {
        this.currencyPair = currencyPair;
    }

    /*getters setters*/

    public CurrencyPair getCurrencyPair() {
        return currencyPair;
    }

    public void setCurrencyPair(CurrencyPair currencyPair) {
        this.currencyPair = currencyPair;
    }

    public String getFirstOrderAmountBase() {
        return firstOrderAmountBase;
    }

    public void setFirstOrderAmountBase(String firstOrderAmountBase) {
        this.firstOrderAmountBase = firstOrderAmountBase;
    }

    public String getFirstOrderRate() {
        return firstOrderRate;
    }

    public void setFirstOrderRate(String firstOrderRate) {
        this.firstOrderRate = firstOrderRate;
    }

    public String getLastOrderAmountBase() {
        return lastOrderAmountBase;
    }

    public void setLastOrderAmountBase(String lastOrderAmountBase) {
        this.lastOrderAmountBase = lastOrderAmountBase;
    }

    public String getLastOrderRate() {
        return lastOrderRate;
    }

    public void setLastOrderRate(String lastOrderRate) {
        this.lastOrderRate = lastOrderRate;
    }

    public String getMinRate() {
        return minRate;
    }

    public void setMinRate(String minRate) {
        this.minRate = minRate;
    }

    public String getMaxRate() {
        return maxRate;
    }

    public void setMaxRate(String maxRate) {
        this.maxRate = maxRate;
    }

    public String getSumBase() {
        return sumBase;
    }

    public void setSumBase(String sumBase) {
        this.sumBase = sumBase;
    }

    public String getSumConvert() {
        return sumConvert;
    }

    public void setSumConvert(String sumConvert) {
        this.sumConvert = sumConvert;
    }
}
