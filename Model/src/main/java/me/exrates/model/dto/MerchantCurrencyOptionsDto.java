package me.exrates.model.dto;

import java.math.BigDecimal;

/**
 * Created by OLEG on 28.11.2016.
 */
public class MerchantCurrencyOptionsDto {
    private Integer merchantId;
    private Integer currencyId;
    private String merchantName;
    private String currencyName;
    private BigDecimal commission;
    private Boolean isRefillBlocked;
    private Boolean isWithdrawBlocked;

    public Integer getMerchantId() {
        return merchantId;
    }

    public void setMerchantId(Integer merchantId) {
        this.merchantId = merchantId;
    }

    public Integer getCurrencyId() {
        return currencyId;
    }

    public void setCurrencyId(Integer currencyId) {
        this.currencyId = currencyId;
    }

    public String getMerchantName() {
        return merchantName;
    }

    public void setMerchantName(String merchantName) {
        this.merchantName = merchantName;
    }

    public String getCurrencyName() {
        return currencyName;
    }

    public void setCurrencyName(String currencyName) {
        this.currencyName = currencyName;
    }

    public BigDecimal getCommission() {
        return commission;
    }

    public void setCommission(BigDecimal commission) {
        this.commission = commission;
    }

    public Boolean getRefillBlocked() {
        return isRefillBlocked;
    }

    public void setRefillBlocked(Boolean refillBlocked) {
        isRefillBlocked = refillBlocked;
    }

    public Boolean getWithdrawBlocked() {
        return isWithdrawBlocked;
    }

    public void setWithdrawBlocked(Boolean withdrawBlocked) {
        isWithdrawBlocked = withdrawBlocked;
    }

    @Override
    public String toString() {
        return "MerchantCurrencyOptionsDto{" +
                "merchantId=" + merchantId +
                ", currencyId=" + currencyId +
                ", merchantName='" + merchantName + '\'' +
                ", currencyName='" + currencyName + '\'' +
                ", commission=" + commission +
                ", isRefillBlocked=" + isRefillBlocked +
                ", isWithdrawBlocked=" + isWithdrawBlocked +
                '}';
    }
}