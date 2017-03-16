package me.exrates.model;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import me.exrates.model.dto.WithdrawRequestCreateDto;
import me.exrates.model.enums.WithdrawalRequestStatus;
import me.exrates.model.enums.invoice.WithdrawStatusEnum;
import me.exrates.model.serializer.LocalDateTimeSerializer;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * @author Denis Savin (pilgrimm333@gmail.com)
 */
@Getter @Setter
@NoArgsConstructor
public class WithdrawRequest {
    private Integer id;
    private Transaction transaction;
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    private LocalDateTime acceptance;
    private Integer processedById;
    private String processedBy;
    private String wallet;
    private Integer userId;
    private String userEmail;
    private MerchantImage merchantImage;
    private WithdrawalRequestStatus status;
    private WithdrawStatusEnum withdrawStatus;
    private String recipientBankName;
    private String recipientBankCode;
    private String userFullName;
    private String remark;
    private BigDecimal amount;
    private BigDecimal commission;

    public WithdrawRequest(WithdrawRequestCreateDto withdrawRequestCreateDto) {
        this.id = withdrawRequestCreateDto.getId();
        this.wallet = withdrawRequestCreateDto.getDestinationWallet();
        this.userId = withdrawRequestCreateDto.getUserId();
        this.userEmail = withdrawRequestCreateDto.getUserEmail();
        this.merchantImage = withdrawRequestCreateDto.getMerchantImage();
        this.withdrawStatus = WithdrawStatusEnum.convert(withdrawRequestCreateDto.getStatusId());
        this.recipientBankName = withdrawRequestCreateDto.getRecipientBankName();
        this.recipientBankCode = withdrawRequestCreateDto.getRecipientBankCode();
        this.userFullName = withdrawRequestCreateDto.getUserFullName();
        this.remark = withdrawRequestCreateDto.getRemark();
        this.amount = withdrawRequestCreateDto.getAmount();
        this.commission = withdrawRequestCreateDto.getCommission();
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
        if (status != that.status) return false;
        if (recipientBankName != null ? !recipientBankName.equals(that.recipientBankName) : that.recipientBankName != null)
            return false;
        if (recipientBankCode != null ? !recipientBankCode.equals(that.recipientBankCode) : that.recipientBankCode != null)
            return false;
        if (userFullName != null ? !userFullName.equals(that.userFullName) : that.userFullName != null) return false;
        return remark != null ? remark.equals(that.remark) : that.remark == null;
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
        result = 31 * result + (recipientBankName != null ? recipientBankName.hashCode() : 0);
        result = 31 * result + (recipientBankCode != null ? recipientBankCode.hashCode() : 0);
        result = 31 * result + (userFullName != null ? userFullName.hashCode() : 0);
        result = 31 * result + (remark != null ? remark.hashCode() : 0);
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
                ", recipientBankName='" + recipientBankName + '\'' +
                ", recipientBankCode='" + recipientBankCode + '\'' +
                ", userFullName='" + userFullName + '\'' +
                ", remark='" + remark + '\'' +
                '}';
    }
}
