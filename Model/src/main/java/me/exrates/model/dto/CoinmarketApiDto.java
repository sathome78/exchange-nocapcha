package me.exrates.model.dto;

import me.exrates.model.util.BigDecimalProcessing;

import java.math.BigDecimal;

/**
 * Created by Valk on 10.05.2016.
 */
public class CoinmarketApiDto {
    private String currency_pair_name;
    private BigDecimal first;
    private BigDecimal last;
    private BigDecimal lowestAsk;
    private BigDecimal highestBid;
    private BigDecimal percentChange;
    private BigDecimal baseVolume;
    private BigDecimal quoteVolume;
    private Integer isFrozen;
    private BigDecimal high24hr;
    private BigDecimal low24hr;

    @Override
    public String toString() {
        return '"'+currency_pair_name.replace('/','_')+"\":" +
                "{\"last\":"+ BigDecimalProcessing.formatToPlainStringQuotes(last) +
                ", \"lowestAsk\":" + BigDecimalProcessing.formatToPlainStringQuotes(lowestAsk) +
                ", \"highestBid\":" + BigDecimalProcessing.formatToPlainStringQuotes(highestBid) +
                ", \"percentChange\":" + BigDecimalProcessing.formatToPlainStringQuotes(percentChange) +
                ", \"baseVolume\":" + BigDecimalProcessing.formatToPlainStringQuotes(baseVolume) +
                ", \"quoteVolume\":" + BigDecimalProcessing.formatToPlainStringQuotes(quoteVolume) +
                ", \"isFrozen\":" + '"'+isFrozen+'"' +
                ", \"high24hr\":" + BigDecimalProcessing.formatToPlainStringQuotes(high24hr) +
                ", \"low24hr\":" + BigDecimalProcessing.formatToPlainStringQuotes(low24hr) +
                '}';
    }

    /*getters setters*/

    public String getCurrency_pair_name() {
        return currency_pair_name;
    }

    public void setCurrency_pair_name(String currency_pair_name) {
        this.currency_pair_name = currency_pair_name;
    }

    public BigDecimal getFirst() {
        return first;
    }

    public void setFirst(BigDecimal first) {
        this.first = first;
    }

    public BigDecimal getLast() {
        return last;
    }

    public void setLast(BigDecimal last) {
        this.last = last;
    }

    public BigDecimal getLowestAsk() {
        return lowestAsk;
    }

    public void setLowestAsk(BigDecimal lowestAsk) {
        this.lowestAsk = lowestAsk;
    }

    public BigDecimal getHighestBid() {
        return highestBid;
    }

    public void setHighestBid(BigDecimal highestBid) {
        this.highestBid = highestBid;
    }

    public BigDecimal getPercentChange() {
        return percentChange;
    }

    public void setPercentChange(BigDecimal percentChange) {
        this.percentChange = percentChange;
    }

    public BigDecimal getBaseVolume() {
        return baseVolume;
    }

    public void setBaseVolume(BigDecimal baseVolume) {
        this.baseVolume = baseVolume;
    }

    public BigDecimal getQuoteVolume() {
        return quoteVolume;
    }

    public void setQuoteVolume(BigDecimal quoteVolume) {
        this.quoteVolume = quoteVolume;
    }

    public Integer getIsFrozen() {
        return isFrozen;
    }

    public void setIsFrozen(Integer isFrozen) {
        this.isFrozen = isFrozen;
    }

    public BigDecimal getHigh24hr() {
        return high24hr;
    }

    public void setHigh24hr(BigDecimal high24hr) {
        this.high24hr = high24hr;
    }

    public BigDecimal getLow24hr() {
        return low24hr;
    }

    public void setLow24hr(BigDecimal low24hr) {
        this.low24hr = low24hr;
    }
}
