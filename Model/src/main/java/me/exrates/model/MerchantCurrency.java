package me.exrates.model;

import java.math.BigDecimal;
import java.util.List;

/**
 * @author Denis Savin (pilgrimm333@gmail.com)
 */
public class MerchantCurrency {

    private int merchantId;
    private int currencyId;
    private String name;
    private String description;
    private BigDecimal minSum;
    private BigDecimal commission;
    private List<MerchantImage> listMerchantImage;

    public int getMerchantId() {
        return merchantId;
    }

    public void setMerchantId(int merchantId) {
        this.merchantId = merchantId;
    }

    public int getCurrencyId() {
        return currencyId;
    }

    public void setCurrencyId(int currencyId) {
        this.currencyId = currencyId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public BigDecimal getMinSum() {
        return minSum;
    }

    public void setMinSum(BigDecimal minSum) {
        this.minSum = minSum;
    }

    public List<MerchantImage> getListMerchantImage() {
        return listMerchantImage;
    }

    public void setListMerchantImage(List<MerchantImage> listMerchantImage) {
        this.listMerchantImage = listMerchantImage;
    }

    public BigDecimal getCommission() {
        return commission;
    }

    public void setCommission(BigDecimal commission) {
        this.commission = commission;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        MerchantCurrency that = (MerchantCurrency) o;

        if (merchantId != that.merchantId) return false;
        if (currencyId != that.currencyId) return false;
        if (name != null ? !name.equals(that.name) : that.name != null) return false;
        if (description != null ? !description.equals(that.description) : that.description != null) return false;
        if (minSum != null ? !minSum.equals(that.minSum) : that.minSum != null) return false;
        if (listMerchantImage != null ? !listMerchantImage.equals(that.listMerchantImage) : that.listMerchantImage != null)
            return false;
        return commission != null ? commission.equals(that.commission) : that.commission == null;

    }

    @Override
    public int hashCode() {
        int result = merchantId;
        result = 31 * result + currencyId;
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (description != null ? description.hashCode() : 0);
        result = 31 * result + (minSum != null ? minSum.hashCode() : 0);
        result = 31 * result + (listMerchantImage != null ? listMerchantImage.hashCode() : 0);
        result = 31 * result + (commission != null ? commission.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "MerchantCurrency{" +
                "merchantId=" + merchantId +
                ", currencyId=" + currencyId +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", minSum=" + minSum +
                ", listMerchantImage=" + listMerchantImage +
                ", commission=" + commission +
                '}';
    }
}