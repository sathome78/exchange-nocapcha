package me.exrates.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * @author Denis Savin (pilgrimm333@gmail.com)
 */
public class CompanyTransaction {

    private int id;
    private int walletId;
    private BigDecimal sum;
    private int currencyId;
    private int operationTypeId;
    private int merchantId;
    private LocalDateTime date;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getWalletId() {
        return walletId;
    }

    public void setWalletId(int walletId) {
        this.walletId = walletId;
    }

    public BigDecimal getSum() {
        return sum;
    }

    public void setSum(BigDecimal sum) {
        this.sum = sum;
    }

    public int getCurrencyId() {
        return currencyId;
    }

    public void setCurrencyId(int currencyId) {
        this.currencyId = currencyId;
    }

    public int getOperationTypeId() {
        return operationTypeId;
    }

    public void setOperationTypeId(int operationTypeId) {
        this.operationTypeId = operationTypeId;
    }

    public int getMerchantId() {
        return merchantId;
    }

    public void setMerchantId(int merchantId) {
        this.merchantId = merchantId;
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

        CompanyTransaction that = (CompanyTransaction) o;

        if (id != that.id) return false;
        if (walletId != that.walletId) return false;
        if (currencyId != that.currencyId) return false;
        if (operationTypeId != that.operationTypeId) return false;
        if (merchantId != that.merchantId) return false;
        if (sum != null ? !sum.equals(that.sum) : that.sum != null) return false;
        return date != null ? date.equals(that.date) : that.date == null;

    }

    @Override
    public int hashCode() {
        int result = id;
        result = 31 * result + walletId;
        result = 31 * result + (sum != null ? sum.hashCode() : 0);
        result = 31 * result + currencyId;
        result = 31 * result + operationTypeId;
        result = 31 * result + merchantId;
        result = 31 * result + (date != null ? date.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "CompanyTransaction{" +
                "merchantId=" + merchantId +
                ", date=" + date +
                ", operationTypeId=" + operationTypeId +
                ", currencyId=" + currencyId +
                ", sum=" + sum +
                ", walletId=" + walletId +
                ", id=" + id +
                '}';
    }
}