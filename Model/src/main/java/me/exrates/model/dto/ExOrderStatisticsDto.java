package me.exrates.model.dto;

import me.exrates.model.CurrencyPair;

import java.math.BigDecimal;

/**
 * Created by Valk on 12.04.16.
 */
public class ExOrderStatisticsDto {
    private CurrencyPair currencyPair;
    private BigDecimal firstOrderAmountBase = BigDecimal.ZERO;
    private BigDecimal firstOrderRate = BigDecimal.ZERO;
    private BigDecimal lastOrderAmountBase = BigDecimal.ZERO;
    private BigDecimal lastOrderRate = BigDecimal.ZERO;
    private BigDecimal minRate = BigDecimal.ZERO;
    private BigDecimal maxRate = BigDecimal.ZERO;
    private BigDecimal sumBase = BigDecimal.ZERO;
    private BigDecimal sumConvert = BigDecimal.ZERO;

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

    public BigDecimal getFirstOrderAmountBase() {
        return firstOrderAmountBase;
    }

    public void setFirstOrderAmountBase(BigDecimal firstOrderAmountBase) {
        this.firstOrderAmountBase = firstOrderAmountBase;
    }

    public BigDecimal getFirstOrderRate() {
        return firstOrderRate;
    }

    public void setFirstOrderRate(BigDecimal firstOrderRate) {
        this.firstOrderRate = firstOrderRate;
    }

    public BigDecimal getLastOrderAmountBase() {
        return lastOrderAmountBase;
    }

    public void setLastOrderAmountBase(BigDecimal lastOrderAmountBase) {
        this.lastOrderAmountBase = lastOrderAmountBase;
    }

    public BigDecimal getLastOrderRate() {
        return lastOrderRate;
    }

    public void setLastOrderRate(BigDecimal lastOrderRate) {
        this.lastOrderRate = lastOrderRate;
    }

    public BigDecimal getMinRate() {
        return minRate;
    }

    public void setMinRate(BigDecimal minRate) {
        this.minRate = minRate;
    }

    public BigDecimal getMaxRate() {
        return maxRate;
    }

    public void setMaxRate(BigDecimal maxRate) {
        this.maxRate = maxRate;
    }

    public BigDecimal getSumBase() {
        return sumBase;
    }

    public void setSumBase(BigDecimal sumBase) {
        this.sumBase = sumBase;
    }

    public BigDecimal getSumConvert() {
        return sumConvert;
    }

    public void setSumConvert(BigDecimal sumConvert) {
        this.sumConvert = sumConvert;
    }
}
