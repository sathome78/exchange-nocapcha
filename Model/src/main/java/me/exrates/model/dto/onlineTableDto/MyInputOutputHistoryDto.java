package me.exrates.model.dto.onlineTableDto;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Getter;
import lombok.Setter;
import me.exrates.model.serializer.LocalDateTimeSerializer;

import java.time.LocalDateTime;

/**
 * Created by Ajet on 23.07.2016.
 */
@Getter @Setter
public class MyInputOutputHistoryDto extends OnlineTableDto {
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    private LocalDateTime datetime;
    private String currencyName;
    private String amount;
    private String commissionAmount;
    private String merchantName;
    private String operationType;
    private Integer transactionId;
    private String transactionProvided;
    private Integer userId;
    private Boolean confirmationRequired;
    private String bankAccount;
    private Integer invoiceRequestStatusId;
    private LocalDateTime statusUpdateDate;
    private String summaryStatus;

    public MyInputOutputHistoryDto() {
        this.needRefresh = true;
    }

    public MyInputOutputHistoryDto(boolean needRefresh) {
        this.needRefresh = needRefresh;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        MyInputOutputHistoryDto that = (MyInputOutputHistoryDto) o;

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
        if (confirmationRequired != null ? !confirmationRequired.equals(that.confirmationRequired) : that.confirmationRequired != null)
            return false;
        if (invoiceRequestStatusId != null ? !invoiceRequestStatusId.equals(that.invoiceRequestStatusId) : that.invoiceRequestStatusId != null)
            return false;
        return transactionProvided != null ? transactionProvided.equals(that.transactionProvided) : that.transactionProvided == null;

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
        result = 31 * result + (confirmationRequired != null ? confirmationRequired.hashCode() : 0);
        result = 31 * result + (invoiceRequestStatusId != null ? invoiceRequestStatusId.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "MyInputOutputHistoryDto{" +
                "datetime=" + datetime +
                ", currencyName='" + currencyName + '\'' +
                ", amount='" + amount + '\'' +
                ", commissionAmount='" + commissionAmount + '\'' +
                ", merchantName='" + merchantName + '\'' +
                ", operationType='" + operationType + '\'' +
                ", transactionId=" + transactionId +
                ", transactionProvided='" + transactionProvided + '\'' +
                ", userId=" + userId +
                ", confirmationRequired=" + confirmationRequired +
                ", bankAccount='" + bankAccount + '\'' +
                '}';
    }
}
