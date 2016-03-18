package me.exrates.model;

import java.math.BigDecimal;

/**
 * @author Denis Savin (pilgrimm333@gmail.com)
 */
public class BTCTransaction {

    private String hash;
    private BigDecimal amount;
    private int transactionId;

    public String getHash() {
        return hash;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public int getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(int transactionId) {
        this.transactionId = transactionId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        BTCTransaction that = (BTCTransaction) o;

        if (transactionId != that.transactionId) return false;
        if (hash != null ? !hash.equals(that.hash) : that.hash != null) return false;
        return amount != null ? amount.equals(that.amount) : that.amount == null;

    }

    @Override
    public int hashCode() {
        int result = hash != null ? hash.hashCode() : 0;
        result = 31 * result + (amount != null ? amount.hashCode() : 0);
        result = 31 * result + transactionId;
        return result;
    }

    @Override
    public String toString() {
        return "BTCTransaction{" +
                "hash='" + hash + '\'' +
                ", amount=" + amount +
                ", transactionId=" + transactionId +
                '}';
    }
}