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
    private BigDecimal inputCommission;
    private BigDecimal outputCommission;

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

    public BigDecimal getInputCommission() {
        return inputCommission;
    }

    public void setInputCommission(BigDecimal inputCommission) {
        this.inputCommission = inputCommission;
    }

    public BigDecimal getOutputCommission() {
        return outputCommission;
    }

    public void setOutputCommission(BigDecimal outputCommission) {
        this.outputCommission = outputCommission;
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


}
