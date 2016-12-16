package me.exrates.model.dto.mobileApiDto;

import me.exrates.model.MerchantImage;

import java.math.BigDecimal;
import java.util.List;

/**
 * Created by OLEG on 13.10.2016.
 */
public class MerchantCurrencyApiDto {
    private Integer merchantId;
    private Integer currencyId;
    private String name;
    private BigDecimal minInputSum;
    private BigDecimal minOutputSum;
    private BigDecimal commission;
    private Boolean isWithdrawBlocked;
    private Boolean isRefillBlocked;
    private List<MerchantImageShortenedDto> listMerchantImage;

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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public BigDecimal getMinInputSum() {
        return minInputSum;
    }

    public void setMinInputSum(BigDecimal minInputSum) {
        this.minInputSum = minInputSum;
    }

    public BigDecimal getMinOutputSum() {
        return minOutputSum;
    }

    public void setMinOutputSum(BigDecimal minOutputSum) {
        this.minOutputSum = minOutputSum;
    }

    public List<MerchantImageShortenedDto> getListMerchantImage() {
        return listMerchantImage;
    }

    public void setListMerchantImage(List<MerchantImageShortenedDto> listMerchantImage) {
        this.listMerchantImage = listMerchantImage;
    }

    public BigDecimal getCommission() {
        return commission;
    }

    public void setCommission(BigDecimal commission) {
        this.commission = commission;
    }

    public Boolean getWithdrawBlocked() {
        return isWithdrawBlocked;
    }

    public void setWithdrawBlocked(Boolean withdrawBlocked) {
        isWithdrawBlocked = withdrawBlocked;
    }

    public Boolean getRefillBlocked() {
        return isRefillBlocked;
    }

    public void setRefillBlocked(Boolean refillBlocked) {
        isRefillBlocked = refillBlocked;
    }

    @Override
    public String toString() {
        return "MerchantCurrencyApiDto{" +
                "merchantId=" + merchantId +
                ", currencyId=" + currencyId +
                ", name='" + name + '\'' +
                ", minInputSum=" + minInputSum +
                ", minOutputSum=" + minOutputSum +
                ", commission=" + commission +
                ", isWithdrawBlocked=" + isWithdrawBlocked +
                ", isRefillBlocked=" + isRefillBlocked +
                ", listMerchantImage=" + listMerchantImage +
                '}';
    }
}
