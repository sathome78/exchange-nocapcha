package me.exrates.model.dto;

import me.exrates.model.CurrencyPair;

import java.math.BigDecimal;

/**
 * Created by Valk
 */
public class ExOrderStatisticsShortByPairsDto {
    private boolean needRefresh;
    private String currencyPairName;
    private String lastOrderRate;
    private String predLastOrderRate;

    public ExOrderStatisticsShortByPairsDto() {
        this.needRefresh = true;
    }

    public ExOrderStatisticsShortByPairsDto(boolean needRefresh) {
        this.needRefresh = needRefresh;
    }

    /*hash*/
    @Override
    public int hashCode() {
        int result = currencyPairName != null ? currencyPairName.hashCode() : 0;
        result = 31 * result + (lastOrderRate != null ? lastOrderRate.hashCode() : 0);
        result = 31 * result + (predLastOrderRate != null ? predLastOrderRate.hashCode() : 0);
        return result;
    }
/*getters setters*/

    public boolean isNeedRefresh() {
        return needRefresh;
    }

    public void setNeedRefresh(boolean needRefresh) {
        this.needRefresh = needRefresh;
    }

    public String getCurrencyPairName() {
        return currencyPairName;
    }

    public void setCurrencyPairName(String currencyPairName) {
        this.currencyPairName = currencyPairName;
    }

    public String getLastOrderRate() {
        return lastOrderRate;
    }

    public void setLastOrderRate(String lastOrderRate) {
        this.lastOrderRate = lastOrderRate;
    }

    public String getPredLastOrderRate() {
        return predLastOrderRate;
    }

    public void setPredLastOrderRate(String predLastOrderRate) {
        this.predLastOrderRate = predLastOrderRate;
    }
}
