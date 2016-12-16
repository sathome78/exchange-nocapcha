package me.exrates.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Created by OLEG on 14.12.2016.
 */
public class StockExchangeRate {

    private Long id;
    private Integer currencyPairId;
    private Integer stockExchangeId;
    private BigDecimal exrate;
    private LocalDateTime date;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getCurrencyPairId() {
        return currencyPairId;
    }

    public void setCurrencyPairId(Integer currencyPairId) {
        this.currencyPairId = currencyPairId;
    }

    public Integer getStockExchangeId() {
        return stockExchangeId;
    }

    public void setStockExchangeId(Integer stockExchangeIn) {
        this.stockExchangeId = stockExchangeIn;
    }

    public BigDecimal getExrate() {
        return exrate;
    }

    public void setExrate(BigDecimal exrate) {
        this.exrate = exrate;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }

    @Override
    public String toString() {
        return "StockExchangeRate{" +
                "id=" + id +
                ", currencyPairId=" + currencyPairId +
                ", stockExchangeId=" + stockExchangeId +
                ", exrate=" + exrate +
                ", date=" + date +
                '}';
    }
}
