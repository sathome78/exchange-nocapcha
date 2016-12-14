package me.exrates.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Created by OLEG on 14.12.2016.
 */
public class StockExchangeRate {

    private Long id;
    private CurrencyPair currencyPair;
    private StockExchange stockExchange;
    private BigDecimal exrate;
    private LocalDateTime date;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public CurrencyPair getCurrencyPair() {
        return currencyPair;
    }

    public void setCurrencyPair(CurrencyPair currencyPair) {
        this.currencyPair = currencyPair;
    }

    public StockExchange getStockExchange() {
        return stockExchange;
    }

    public void setStockExchange(StockExchange stockExchange) {
        this.stockExchange = stockExchange;
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
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        StockExchangeRate that = (StockExchangeRate) o;

        if (id != null ? !id.equals(that.id) : that.id != null) return false;
        if (currencyPair != null ? !currencyPair.equals(that.currencyPair) : that.currencyPair != null) return false;
        if (stockExchange != null ? !stockExchange.equals(that.stockExchange) : that.stockExchange != null)
            return false;
        if (exrate != null ? !exrate.equals(that.exrate) : that.exrate != null) return false;
        return date != null ? date.equals(that.date) : that.date == null;
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (currencyPair != null ? currencyPair.hashCode() : 0);
        result = 31 * result + (stockExchange != null ? stockExchange.hashCode() : 0);
        result = 31 * result + (exrate != null ? exrate.hashCode() : 0);
        result = 31 * result + (date != null ? date.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "StockExchangeRate{" +
                "id=" + id +
                ", currencyPair=" + currencyPair +
                ", stockExchange=" + stockExchange +
                ", exrate=" + exrate +
                ", date=" + date +
                '}';
    }
}
