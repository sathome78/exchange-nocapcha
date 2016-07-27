package me.exrates.model;

import java.time.LocalDateTime;

/**
 * Created by ogolv on 25.07.2016.
 */
public class InvoiceRequest {

    private Transaction transaction;
    private String userEmail;
    private String acceptanceUserEmail;
    private LocalDateTime acceptanceTime;

    public InvoiceRequest() {
    }

    public InvoiceRequest(Transaction transaction, String userEmail, String acceptanceUserEmail, LocalDateTime acceptanceTime) {
        this.transaction = transaction;
        this.userEmail = userEmail;
        this.acceptanceUserEmail = acceptanceUserEmail;
        this.acceptanceTime = acceptanceTime;
    }

    public Transaction getTransaction() {
        return transaction;
    }

    public void setTransaction(Transaction transaction) {
        this.transaction = transaction;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public String getAcceptanceUserEmail() {
        return acceptanceUserEmail;
    }

    public void setAcceptanceUserEmail(String acceptanceUserEmail) {
        this.acceptanceUserEmail = acceptanceUserEmail;
    }

    public LocalDateTime getAcceptanceTime() {
        return acceptanceTime;
    }

    public void setAcceptanceTime(LocalDateTime acceptanceTime) {
        this.acceptanceTime = acceptanceTime;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        InvoiceRequest that = (InvoiceRequest) o;

        if (transaction != null ? !transaction.equals(that.transaction) : that.transaction != null) return false;
        if (userEmail != null ? !userEmail.equals(that.userEmail) : that.userEmail != null) return false;
        if (acceptanceUserEmail != null ? !acceptanceUserEmail.equals(that.acceptanceUserEmail) : that.acceptanceUserEmail != null)
            return false;
        return acceptanceTime != null ? acceptanceTime.equals(that.acceptanceTime) : that.acceptanceTime == null;

    }

    @Override
    public int hashCode() {
        int result = transaction != null ? transaction.hashCode() : 0;
        result = 31 * result + (userEmail != null ? userEmail.hashCode() : 0);
        result = 31 * result + (acceptanceUserEmail != null ? acceptanceUserEmail.hashCode() : 0);
        result = 31 * result + (acceptanceTime != null ? acceptanceTime.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "InvoiceRequest{" +
                "transaction=" + transaction +
                ", userEmail='" + userEmail + '\'' +
                ", acceptanceUserEmail='" + acceptanceUserEmail + '\'' +
                ", acceptanceTime=" + acceptanceTime +
                '}';
    }
}
