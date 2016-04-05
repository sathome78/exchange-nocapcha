package me.exrates.model;

import java.time.LocalDateTime;

/**
 * @author Denis Savin (pilgrimm333@gmail.com)
 */
public class WithdrawRequest {

    private Transaction transaction;
    private LocalDateTime acceptance;
    private String processedBy;
    private String wallet;
    private String userEmail;

    public Transaction getTransaction() {
        return transaction;
    }

    public void setTransaction(Transaction transaction) {
        this.transaction = transaction;
    }

    public LocalDateTime getAcceptance() {
        return acceptance;
    }

    public void setAcceptance(LocalDateTime acceptance) {
        this.acceptance = acceptance;
    }

    public String getProcessedBy() {
        return processedBy;
    }

    public void setProcessedBy(String processedBy) {
        this.processedBy = processedBy;
    }

    public String getWallet() {
        return wallet;
    }

    public void setWallet(String wallet) {
        this.wallet = wallet;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        WithdrawRequest request = (WithdrawRequest) o;

        if (transaction != null ? !transaction.equals(request.transaction) : request.transaction != null) return false;
        if (acceptance != null ? !acceptance.equals(request.acceptance) : request.acceptance != null) return false;
        if (processedBy != null ? !processedBy.equals(request.processedBy) : request.processedBy != null) return false;
        if (wallet != null ? !wallet.equals(request.wallet) : request.wallet != null) return false;
        return userEmail != null ? userEmail.equals(request.userEmail) : request.userEmail == null;

    }

    @Override
    public int hashCode() {
        int result = transaction != null ? transaction.hashCode() : 0;
        result = 31 * result + (acceptance != null ? acceptance.hashCode() : 0);
        result = 31 * result + (processedBy != null ? processedBy.hashCode() : 0);
        result = 31 * result + (wallet != null ? wallet.hashCode() : 0);
        result = 31 * result + (userEmail != null ? userEmail.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "WithdrawRequest{" +
                "transaction=" + transaction +
                ", acceptance=" + acceptance +
                ", processedBy='" + processedBy + '\'' +
                ", wallet='" + wallet + '\'' +
                ", userEmail='" + userEmail + '\'' +
                '}';
    }
}
