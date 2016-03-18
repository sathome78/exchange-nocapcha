package me.exrates.model;

import java.math.BigDecimal;

/**
 * @author Denis Savin (pilgrimm333@gmail.com)
 */
public class BlockchainPayment {

    private BigDecimal amount;
    private String address;
    private int invoiceId;
    private String secret;

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public int getInvoiceId() {
        return invoiceId;
    }

    public void setInvoiceId(int invoiceId) {
        this.invoiceId = invoiceId;
    }

    public String getSecret() {
        return secret;
    }

    public void setSecret(String secret) {
        this.secret = secret;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        BlockchainPayment that = (BlockchainPayment) o;

        if (invoiceId != that.invoiceId) return false;
        if (amount != null ? !amount.equals(that.amount) : that.amount != null) return false;
        if (address != null ? !address.equals(that.address) : that.address != null) return false;
        return secret != null ? secret.equals(that.secret) : that.secret == null;

    }

    @Override
    public int hashCode() {
        int result = amount != null ? amount.hashCode() : 0;
        result = 31 * result + (address != null ? address.hashCode() : 0);
        result = 31 * result + invoiceId;
        result = 31 * result + (secret != null ? secret.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "BlockchainPayment{" +
                "amount=" + amount +
                ", address='" + address + '\'' +
                ", invoiceId=" + invoiceId +
                ", secret='" + secret + '\'' +
                '}';
    }
}