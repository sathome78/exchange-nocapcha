package me.exrates.model;

import java.time.LocalDateTime;

/**
 * @author Denis Savin (pilgrimm333@gmail.com)
 */
public class CompanyAccount {

    private int id;
    private int walletId;
    private int commissionId;
    private double amount;
    private TransactionType transactionType;
    private LocalDateTime date;

    public enum TransactionType {

        DEBIT(1),
        CREDIT(0);

        public final int operation;

        TransactionType(int operation) {
            this.operation = operation;
        }

        public int getOperation() {
            return operation;
        }
    }

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

    public int getCommissionId() {
        return commissionId;
    }

    public void setCommissionId(int commissionId) {
        this.commissionId = commissionId;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public TransactionType getTransactionType() {
        return transactionType;
    }

    public void setTransactionType(TransactionType transactionType) {
        this.transactionType = transactionType;
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

        CompanyAccount that = (CompanyAccount) o;

        if (id != that.id) return false;
        if (walletId != that.walletId) return false;
        if (commissionId != that.commissionId) return false;
        if (Double.compare(that.amount, amount) != 0) return false;
        if (transactionType != that.transactionType) return false;
        return date != null ? date.equals(that.date) : that.date == null;

    }

    @Override
    public int hashCode() {
        int result;
        long temp;
        result = id;
        result = 31 * result + walletId;
        result = 31 * result + commissionId;
        temp = Double.doubleToLongBits(amount);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        result = 31 * result + (transactionType != null ? transactionType.hashCode() : 0);
        result = 31 * result + (date != null ? date.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "CompanyAccount{" +
                "id=" + id +
                ", walletId=" + walletId +
                ", commissionId=" + commissionId +
                ", amount=" + amount +
                ", transactionType=" + transactionType +
                ", date=" + date +
                '}';
    }
}