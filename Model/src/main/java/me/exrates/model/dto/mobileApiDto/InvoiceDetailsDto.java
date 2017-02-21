package me.exrates.model.dto.mobileApiDto;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import me.exrates.model.InvoiceBank;
import me.exrates.model.InvoiceRequest;
import me.exrates.model.enums.InvoiceRequestStatusEnum;

import me.exrates.model.serializer.LocalDateTimeToLongSerializer;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Created by OLEG on 21.02.2017.
 */
public class InvoiceDetailsDto {

    private Integer id;
    private Integer currencyId;
    private Double amount;
    private Double commissionAmount;
    @JsonSerialize(using = LocalDateTimeToLongSerializer.class)
    private LocalDateTime creationTime;
    @JsonSerialize(using = LocalDateTimeToLongSerializer.class)
    private LocalDateTime acceptanceTime;
    private Integer targetBankId;
    private String userFullName;
    private String remark;
    private String payerBankName;
    private String payerBankCode;
    private String payerAccount;
    private InvoiceRequestStatusEnum invoiceRequestStatus;
    @JsonSerialize(using = LocalDateTimeToLongSerializer.class)
    private LocalDateTime statusUpdateDate;
    private String receiptScanPath;

    public InvoiceDetailsDto() {
    }

    public InvoiceDetailsDto(InvoiceRequest invoiceRequest) {
        this.id = invoiceRequest.getTransaction().getId();
        this.currencyId = invoiceRequest.getTransaction().getCurrency().getId();
        this.amount = invoiceRequest.getTransaction().getAmount().doubleValue();
        this.commissionAmount = invoiceRequest.getTransaction().getCommissionAmount().doubleValue();
        this.creationTime = invoiceRequest.getTransaction().getDatetime();
        this.acceptanceTime = invoiceRequest.getAcceptanceTime();
        this.targetBankId = invoiceRequest.getInvoiceBank().getId();
        this.userFullName = invoiceRequest.getUserFullName();
        this.remark = invoiceRequest.getRemark();
        this.payerBankName = invoiceRequest.getPayerBankName();
        this.payerBankCode = invoiceRequest.getPayerBankCode();
        this.payerAccount = invoiceRequest.getPayerAccount();
        this.invoiceRequestStatus = invoiceRequest.getInvoiceRequestStatus();
        this.statusUpdateDate = invoiceRequest.getStatusUpdateDate();
        this.receiptScanPath = invoiceRequest.getReceiptScanPath();
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getCurrencyId() {
        return currencyId;
    }

    public void setCurrencyId(Integer currencyId) {
        this.currencyId = currencyId;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public Double getCommissionAmount() {
        return commissionAmount;
    }

    public void setCommissionAmount(Double commissionAmount) {
        this.commissionAmount = commissionAmount;
    }

    public LocalDateTime getCreationTime() {
        return creationTime;
    }

    public void setCreationTime(LocalDateTime creationTime) {
        this.creationTime = creationTime;
    }

    public LocalDateTime getAcceptanceTime() {
        return acceptanceTime;
    }

    public void setAcceptanceTime(LocalDateTime acceptanceTime) {
        this.acceptanceTime = acceptanceTime;
    }

    public Integer getTargetBankId() {
        return targetBankId;
    }

    public void setTargetBankId(Integer targetBankId) {
        this.targetBankId = targetBankId;
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

    public String getPayerBankName() {
        return payerBankName;
    }

    public void setPayerBankName(String payerBankName) {
        this.payerBankName = payerBankName;
    }

    public String getPayerBankCode() {
        return payerBankCode;
    }

    public void setPayerBankCode(String payerBankCode) {
        this.payerBankCode = payerBankCode;
    }

    public String getPayerAccount() {
        return payerAccount;
    }

    public void setPayerAccount(String payerAccount) {
        this.payerAccount = payerAccount;
    }

    public InvoiceRequestStatusEnum getInvoiceRequestStatus() {
        return invoiceRequestStatus;
    }

    public void setInvoiceRequestStatus(InvoiceRequestStatusEnum invoiceRequestStatus) {
        this.invoiceRequestStatus = invoiceRequestStatus;
    }

    public LocalDateTime getStatusUpdateDate() {
        return statusUpdateDate;
    }

    public void setStatusUpdateDate(LocalDateTime statusUpdateDate) {
        this.statusUpdateDate = statusUpdateDate;
    }

    public String getReceiptScanPath() {
        return receiptScanPath;
    }

    public void setReceiptScanPath(String receiptScanPath) {
        this.receiptScanPath = receiptScanPath;
    }

    @Override
    public String toString() {
        return "InvoiceDetailsDto{" +
                "id=" + id +
                ", currencyId=" + currencyId +
                ", amount=" + amount +
                ", commissionAmount=" + commissionAmount +
                ", creationTime=" + creationTime +
                ", acceptanceTime=" + acceptanceTime +
                ", targetBankId=" + targetBankId +
                ", userFullName='" + userFullName + '\'' +
                ", remark='" + remark + '\'' +
                ", payerBankName='" + payerBankName + '\'' +
                ", payerBankCode='" + payerBankCode + '\'' +
                ", payerAccount='" + payerAccount + '\'' +
                ", invoiceRequestStatus=" + invoiceRequestStatus +
                ", statusUpdateDate=" + statusUpdateDate +
                ", receiptScanPath='" + receiptScanPath + '\'' +
                '}';
    }
}
