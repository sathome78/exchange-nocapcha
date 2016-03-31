package me.exrates.model;

import java.util.Optional;

/**
 * @author Denis Savin (pilgrimm333@gmail.com)
 */
public class PendingPayment {

    private int invoiceId;
    private String transactionHash;
    private String address;

    public PendingPayment() {
    }

    public int getInvoiceId() {
        return invoiceId;
    }

    public void setInvoiceId(final int invoiceId) {
        this.invoiceId = invoiceId;
    }

    public String getTransactionHash() {
        return transactionHash;
    }

    public void setTransactionHash(final String transactionHash) {
        this.transactionHash = transactionHash;
    }

    public Optional<String> getAddress() {
        return Optional.ofNullable(address);
    }

    public void setAddress(final String address) {
        this.address = address;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        final PendingPayment that = (PendingPayment) o;

        if (invoiceId != that.invoiceId) return false;
        if (transactionHash != null ? !transactionHash.equals(that.transactionHash) : that.transactionHash != null)
            return false;
        return address != null ? address.equals(that.address) : that.address == null;

    }

    @Override
    public int hashCode() {
        int result = invoiceId;
        result = 31 * result + (transactionHash != null ? transactionHash.hashCode() : 0);
        result = 31 * result + (address != null ? address.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "PendingPayment{" +
            "invoiceId=" + invoiceId +
            ", transactionHash='" + transactionHash + '\'' +
            ", address='" + address + '\'' +
            '}';
    }
}
