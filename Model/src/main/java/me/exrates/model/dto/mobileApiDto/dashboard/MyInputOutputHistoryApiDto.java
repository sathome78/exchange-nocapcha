package me.exrates.model.dto.mobileApiDto.dashboard;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import me.exrates.model.dto.onlineTableDto.MyInputOutputHistoryDto;
import me.exrates.model.serializer.LocalDateTimeToLongSerializer;
import me.exrates.model.util.BigDecimalProcessing;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Locale;

/**
 * Created by Ajet on 23.07.2016.
 */
public class MyInputOutputHistoryApiDto {
    @JsonSerialize(using = LocalDateTimeToLongSerializer.class)
    private LocalDateTime datetime;
    private String currencyName;
    private Double amount;
    private Double commissionAmount;
    private String merchantName;
    private String operationType;
    private Integer transactionId;
    private String transactionProvided;
    private Integer userId;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String bankAccount;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Boolean invoiceConfirmed;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String userFullName;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String remark;

    public MyInputOutputHistoryApiDto(MyInputOutputHistoryDto dto, Locale locale) {
        this.datetime = dto.getDatetime();
        this.currencyName = dto.getCurrencyName();
        this.amount = BigDecimalProcessing.parseLocale(dto.getAmount(), locale, 2).doubleValue();
        this.commissionAmount = BigDecimalProcessing.parseLocale(dto.getCommissionAmount(), locale, 2).doubleValue();;
        this.merchantName = dto.getMerchantName();
        this.operationType = dto.getOperationType();
        this.transactionId = dto.getTransactionId();
        this.transactionProvided = dto.getTransactionProvided();
        this.userId = dto.getUserId();
        this.invoiceConfirmed = "Invoice".equals(dto.getMerchantName()) ? !dto.getConfirmationRequired() : null;
        this.bankAccount = dto.getBankAccount();
        this.userFullName = dto.getUserFullName();
        this.remark = dto.getRemark();
    }

    public LocalDateTime getDatetime() {
        return datetime;
    }

    public void setDatetime(LocalDateTime datetime) {
        this.datetime = datetime;
    }

    public String getCurrencyName() {
        return currencyName;
    }

    public void setCurrencyName(String currencyName) {
        this.currencyName = currencyName;
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

    public String getMerchantName() {
        return merchantName;
    }

    public void setMerchantName(String merchantName) {
        this.merchantName = merchantName;
    }

    public String getOperationType() {
        return operationType;
    }

    public void setOperationType(String operationType) {
        this.operationType = operationType;
    }

    public Integer getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(Integer transactionId) {
        this.transactionId = transactionId;
    }

    public String getTransactionProvided() {
        return transactionProvided;
    }

    public void setTransactionProvided(String transactionProvided) {
        this.transactionProvided = transactionProvided;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getBankAccount() {
        return bankAccount;
    }

    public void setBankAccount(String bankAccount) {
        this.bankAccount = bankAccount;
    }

    public Boolean getInvoiceConfirmed() {
        return invoiceConfirmed;
    }

    public void setInvoiceConfirmed(Boolean invoiceConfirmed) {
        this.invoiceConfirmed = invoiceConfirmed;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        MyInputOutputHistoryApiDto that = (MyInputOutputHistoryApiDto) o;

        if (datetime != null ? !datetime.equals(that.datetime) : that.datetime != null) return false;
        if (currencyName != null ? !currencyName.equals(that.currencyName) : that.currencyName != null) return false;
        if (amount != null ? !amount.equals(that.amount) : that.amount != null) return false;
        if (commissionAmount != null ? !commissionAmount.equals(that.commissionAmount) : that.commissionAmount != null)
            return false;
        if (merchantName != null ? !merchantName.equals(that.merchantName) : that.merchantName != null) return false;
        if (operationType != null ? !operationType.equals(that.operationType) : that.operationType != null)
            return false;
        if (transactionId != null ? !transactionId.equals(that.transactionId) : that.transactionId != null)
            return false;
        if (transactionProvided != null ? !transactionProvided.equals(that.transactionProvided) : that.transactionProvided != null)
            return false;
        if (userId != null ? !userId.equals(that.userId) : that.userId != null) return false;
        if (bankAccount != null ? !bankAccount.equals(that.bankAccount) : that.bankAccount != null) return false;
        if (invoiceConfirmed != null ? !invoiceConfirmed.equals(that.invoiceConfirmed) : that.invoiceConfirmed != null)
            return false;
        if (userFullName != null ? !userFullName.equals(that.userFullName) : that.userFullName != null) return false;
        return remark != null ? remark.equals(that.remark) : that.remark == null;
    }

    @Override
    public int hashCode() {
        int result = datetime != null ? datetime.hashCode() : 0;
        result = 31 * result + (currencyName != null ? currencyName.hashCode() : 0);
        result = 31 * result + (amount != null ? amount.hashCode() : 0);
        result = 31 * result + (commissionAmount != null ? commissionAmount.hashCode() : 0);
        result = 31 * result + (merchantName != null ? merchantName.hashCode() : 0);
        result = 31 * result + (operationType != null ? operationType.hashCode() : 0);
        result = 31 * result + (transactionId != null ? transactionId.hashCode() : 0);
        result = 31 * result + (transactionProvided != null ? transactionProvided.hashCode() : 0);
        result = 31 * result + (userId != null ? userId.hashCode() : 0);
        result = 31 * result + (bankAccount != null ? bankAccount.hashCode() : 0);
        result = 31 * result + (invoiceConfirmed != null ? invoiceConfirmed.hashCode() : 0);
        result = 31 * result + (userFullName != null ? userFullName.hashCode() : 0);
        result = 31 * result + (remark != null ? remark.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "MyInputOutputHistoryApiDto{" +
                "datetime=" + datetime +
                ", currencyName='" + currencyName + '\'' +
                ", amount=" + amount +
                ", commissionAmount=" + commissionAmount +
                ", merchantName='" + merchantName + '\'' +
                ", operationType='" + operationType + '\'' +
                ", transactionId=" + transactionId +
                ", transactionProvided='" + transactionProvided + '\'' +
                ", userId=" + userId +
                ", bankAccount='" + bankAccount + '\'' +
                ", invoiceConfirmed=" + invoiceConfirmed +
                ", userFullName='" + userFullName + '\'' +
                ", remark='" + remark + '\'' +
                '}';
    }

    public static void main(String[] args) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();

        BigDecimal val = new BigDecimal(1000000000000000.0);

        System.out.println(objectMapper.writeValueAsString(val));
    }

}
