package me.exrates.model;

import java.math.BigDecimal;
import java.util.Date;

public class IEOClaim {
    private int id;
    private String currencyName;
    private int makerId;
    private int userId;
    private BigDecimal amount;
    private Date created;
    private IEOClaimStateEnum state;

    public enum IEOClaimStateEnum {
        created, processed
    }

    public IEOClaim() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCurrencyName() {
        return currencyName;
    }

    public void setCurrencyName(String currencyName) {
        this.currencyName = currencyName;
    }

    public int getMakerId() {
        return makerId;
    }

    public void setMakerId(int makerId) {
        this.makerId = makerId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }

    public IEOClaimStateEnum getState() {
        return state;
    }

    public void setState(IEOClaimStateEnum state) {
        this.state = state;
    }
}
