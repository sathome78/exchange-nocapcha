package me.exrates.model;

import java.time.LocalDateTime;

/**
 * Created by ogolv on 25.07.2016.
 */
public class InvoiceRequest {

    private Transaction transaction;
    private User user;
    private User acceptanceUser;
    private LocalDateTime acceptanceTime;

    public InvoiceRequest() {
    }

    public InvoiceRequest(Transaction transaction, User user, User acceptanceUser, LocalDateTime acceptanceTime) {
        this.transaction = transaction;
        this.user = user;
        this.acceptanceUser = acceptanceUser;
        this.acceptanceTime = acceptanceTime;
    }

    public Transaction getTransaction() {
        return transaction;
    }

    public void setTransaction(Transaction transaction) {
        this.transaction = transaction;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public User getAcceptanceUser() {
        return acceptanceUser;
    }

    public void setAcceptanceUser(User acceptanceUser) {
        this.acceptanceUser = acceptanceUser;
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
        if (user != null ? !user.equals(that.user) : that.user != null) return false;
        if (acceptanceUser != null ? !acceptanceUser.equals(that.acceptanceUser) : that.acceptanceUser != null)
            return false;
        return acceptanceTime != null ? acceptanceTime.equals(that.acceptanceTime) : that.acceptanceTime == null;

    }

    @Override
    public int hashCode() {
        int result = transaction != null ? transaction.hashCode() : 0;
        result = 31 * result + (user != null ? user.hashCode() : 0);
        result = 31 * result + (acceptanceUser != null ? acceptanceUser.hashCode() : 0);
        result = 31 * result + (acceptanceTime != null ? acceptanceTime.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "InvoiceRequest{" +
                "transaction=" + transaction +
                ", user=" + user +
                ", acceptanceUser=" + acceptanceUser +
                ", acceptanceTime=" + acceptanceTime +
                '}';
    }
}
