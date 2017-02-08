package me.exrates.model;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import me.exrates.model.serializer.LocalDateTimeSerializer;

import java.time.LocalDateTime;

/**
 * Created by ogolv on 25.07.2016.
 */
public class InvoiceRequest {

    private Transaction transaction;
    private Integer userId;
    private String userEmail;
    private Integer acceptanceUserId;
    private String acceptanceUserEmail;
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    private LocalDateTime acceptanceTime;
    private InvoiceBank invoiceBank;
    private String userFullName;
    private String remark;
    private String payeeBankName;
    private String payeeAccount;

    public InvoiceRequest() {
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

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public Integer getAcceptanceUserId() {
        return acceptanceUserId;
    }

    public void setAcceptanceUserId(Integer acceptanceUserId) {
        this.acceptanceUserId = acceptanceUserId;
    }

    public InvoiceBank getInvoiceBank() {
        return invoiceBank;
    }

    public void setInvoiceBank(InvoiceBank invoiceBank) {
        this.invoiceBank = invoiceBank;
    }

    public String getUserFullName() {
        return userFullName;
    }

    public void setUserFullName(String userFullName) {
        this.userFullName = userFullName;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getPayeeBankName() {
        return payeeBankName;
    }

    public void setPayeeBankName(String payeeBankName) {
        this.payeeBankName = payeeBankName;
    }

    public String getPayeeAccount() {
        return payeeAccount;
    }

    public void setPayeeAccount(String payeeAccount) {
        this.payeeAccount = payeeAccount;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        InvoiceRequest that = (InvoiceRequest) o;

        if (transaction != null ? !transaction.equals(that.transaction) : that.transaction != null) return false;
        if (userId != null ? !userId.equals(that.userId) : that.userId != null) return false;
        if (userEmail != null ? !userEmail.equals(that.userEmail) : that.userEmail != null) return false;
        if (acceptanceUserId != null ? !acceptanceUserId.equals(that.acceptanceUserId) : that.acceptanceUserId != null)
            return false;
        if (acceptanceUserEmail != null ? !acceptanceUserEmail.equals(that.acceptanceUserEmail) : that.acceptanceUserEmail != null)
            return false;
        if (acceptanceTime != null ? !acceptanceTime.equals(that.acceptanceTime) : that.acceptanceTime != null)
            return false;
        if (invoiceBank != null ? !invoiceBank.equals(that.invoiceBank) : that.invoiceBank != null) return false;
        if (userFullName != null ? !userFullName.equals(that.userFullName) : that.userFullName != null) return false;
        if (remark != null ? !remark.equals(that.remark) : that.remark != null) return false;
        if (payeeBankName != null ? !payeeBankName.equals(that.payeeBankName) : that.payeeBankName != null)
            return false;
        return payeeAccount != null ? payeeAccount.equals(that.payeeAccount) : that.payeeAccount == null;
    }

    @Override
    public int hashCode() {
        int result = transaction != null ? transaction.hashCode() : 0;
        result = 31 * result + (userId != null ? userId.hashCode() : 0);
        result = 31 * result + (userEmail != null ? userEmail.hashCode() : 0);
        result = 31 * result + (acceptanceUserId != null ? acceptanceUserId.hashCode() : 0);
        result = 31 * result + (acceptanceUserEmail != null ? acceptanceUserEmail.hashCode() : 0);
        result = 31 * result + (acceptanceTime != null ? acceptanceTime.hashCode() : 0);
        result = 31 * result + (invoiceBank != null ? invoiceBank.hashCode() : 0);
        result = 31 * result + (userFullName != null ? userFullName.hashCode() : 0);
        result = 31 * result + (remark != null ? remark.hashCode() : 0);
        result = 31 * result + (payeeBankName != null ? payeeBankName.hashCode() : 0);
        result = 31 * result + (payeeAccount != null ? payeeAccount.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "InvoiceRequest{" +
                "transaction=" + transaction +
                ", userId=" + userId +
                ", userEmail='" + userEmail + '\'' +
                ", acceptanceUserId=" + acceptanceUserId +
                ", acceptanceUserEmail='" + acceptanceUserEmail + '\'' +
                ", acceptanceTime=" + acceptanceTime +
                ", invoiceBank=" + invoiceBank +
                ", userFullName='" + userFullName + '\'' +
                ", remark='" + remark + '\'' +
                ", payeeBankName='" + payeeBankName + '\'' +
                ", payeeAccount='" + payeeAccount + '\'' +
                '}';
    }
}
