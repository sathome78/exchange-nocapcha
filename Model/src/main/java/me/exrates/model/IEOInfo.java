package me.exrates.model;

import me.exrates.model.enums.IEOStatusEnum;

import java.math.BigDecimal;
import java.util.Date;

public class IEOInfo {
    private int currencyId;
    private int userId;
    private BigDecimal rate;
    private BigDecimal amount;
    private String contibutors;
    private Date started;
    private BigDecimal totalLimit;
    private BigDecimal buyLimit;
    private int version;
    private IEOStatusEnum status;

    public IEOInfo() {
    }

    public int getCurrencyId() {
        return currencyId;
    }

    public void setCurrencyId(int currencyId) {
        this.currencyId = currencyId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public BigDecimal getRate() {
        return rate;
    }

    public void setRate(BigDecimal rate) {
        this.rate = rate;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getContibutors() {
        return contibutors;
    }

    public void setContibutors(String contibutors) {
        this.contibutors = contibutors;
    }

    public Date getStarted() {
        return started;
    }

    public void setStarted(Date started) {
        this.started = started;
    }

    public BigDecimal getTotalLimit() {
        return totalLimit;
    }

    public void setTotalLimit(BigDecimal totalLimit) {
        this.totalLimit = totalLimit;
    }

    public BigDecimal getBuyLimit() {
        return buyLimit;
    }

    public void setBuyLimit(BigDecimal buyLimit) {
        this.buyLimit = buyLimit;
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public IEOStatusEnum getStatus() {
        return status;
    }

    public void setStatus(IEOStatusEnum status) {
        this.status = status;
    }
}
