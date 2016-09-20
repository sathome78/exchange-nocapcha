package me.exrates.model;

import me.exrates.model.enums.WithdrawalRequestStatus;

import java.time.LocalDateTime;

/**
 * @author Denis Savin (pilgrimm333@gmail.com)
 */
public class WithdrawRequest {

    private Transaction transaction;
    private LocalDateTime acceptance;
    private Integer processedById;
    private String processedBy;
    private String wallet;
    private Integer userId;
    private String userEmail;
    private MerchantImage merchantImage;
    private WithdrawalRequestStatus status;

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

    public MerchantImage getMerchantImage() {
        return merchantImage;
    }

    public void setMerchantImage(MerchantImage merchantImage) {
        this.merchantImage = merchantImage;
    }

    public Integer getProcessedById() {
        return processedById;
    }

    public void setProcessedById(Integer processedById) {
        this.processedById = processedById;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public WithdrawalRequestStatus getStatus() {
        return status;
    }

    public void setStatus(WithdrawalRequestStatus status) {
        this.status = status;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        WithdrawRequest that = (WithdrawRequest) o;

        if (transaction != null ? !transaction.equals(that.transaction) : that.transaction != null) return false;
        if (acceptance != null ? !acceptance.equals(that.acceptance) : that.acceptance != null) return false;
        if (processedById != null ? !processedById.equals(that.processedById) : that.processedById != null)
            return false;
        if (processedBy != null ? !processedBy.equals(that.processedBy) : that.processedBy != null) return false;
        if (wallet != null ? !wallet.equals(that.wallet) : that.wallet != null) return false;
        if (userId != null ? !userId.equals(that.userId) : that.userId != null) return false;
        if (userEmail != null ? !userEmail.equals(that.userEmail) : that.userEmail != null) return false;
        if (merchantImage != null ? !merchantImage.equals(that.merchantImage) : that.merchantImage != null)
            return false;
        return status == that.status;

    }

    @Override
    public int hashCode() {
        int result = transaction != null ? transaction.hashCode() : 0;
        result = 31 * result + (acceptance != null ? acceptance.hashCode() : 0);
        result = 31 * result + (processedById != null ? processedById.hashCode() : 0);
        result = 31 * result + (processedBy != null ? processedBy.hashCode() : 0);
        result = 31 * result + (wallet != null ? wallet.hashCode() : 0);
        result = 31 * result + (userId != null ? userId.hashCode() : 0);
        result = 31 * result + (userEmail != null ? userEmail.hashCode() : 0);
        result = 31 * result + (merchantImage != null ? merchantImage.hashCode() : 0);
        result = 31 * result + (status != null ? status.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "WithdrawRequest{" +
                "transaction=" + transaction +
                ", acceptance=" + acceptance +
                ", processedById=" + processedById +
                ", processedBy='" + processedBy + '\'' +
                ", wallet='" + wallet + '\'' +
                ", userId=" + userId +
                ", userEmail='" + userEmail + '\'' +
                ", merchantImage=" + merchantImage +
                ", status=" + status +
                '}';
    }
}
