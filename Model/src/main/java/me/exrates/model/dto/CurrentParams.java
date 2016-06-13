package me.exrates.model.dto;

import me.exrates.model.CurrencyPair;

/**
 * Created by Valk on 02.06.2016.
 */
public class CurrentParams {
    private CurrencyPair currencyPair;
    private String period;
    private String chartType;

    /*getters setters*/

    public CurrencyPair getCurrencyPair() {
        return currencyPair;
    }

    public void setCurrencyPair(CurrencyPair currencyPair) {
        this.currencyPair = currencyPair;
    }

    public String getPeriod() {
        return period;
    }

    public void setPeriod(String period) {
        this.period = period;
    }

    public String getChartType() {
        return chartType;
    }

    public void setChartType(String chartType) {
        this.chartType = chartType;
    }
}
