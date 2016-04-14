package me.exrates.model.dto;

import java.math.BigDecimal;

/**
 * Created by Valk on 14.04.16.
 */
public class OrderListDto {
    private Integer id;
    private BigDecimal exrate;
    private BigDecimal currencyAmount1;
    private BigDecimal currencyAmount2;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public BigDecimal getExrate() {
        return exrate;
    }

    public void setExrate(BigDecimal exrate) {
        this.exrate = exrate;
    }

    public BigDecimal getCurrencyAmount1() {
        return currencyAmount1;
    }

    public void setCurrencyAmount1(BigDecimal currencyAmount1) {
        this.currencyAmount1 = currencyAmount1;
    }

    public BigDecimal getCurrencyAmount2() {
        return currencyAmount2;
    }

    public void setCurrencyAmount2(BigDecimal currencyAmount2) {
        this.currencyAmount2 = currencyAmount2;
    }
}
