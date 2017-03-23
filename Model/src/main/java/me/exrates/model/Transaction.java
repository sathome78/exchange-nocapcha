package me.exrates.model;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Getter;
import lombok.Setter;
import me.exrates.model.enums.OperationType;
import me.exrates.model.enums.TransactionSourceType;
import me.exrates.model.serializer.LocalDateTimeSerializer;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * @author Denis Savin (pilgrimm333@gmail.com)
 */
@Getter @Setter
public class Transaction {

    private int id;
    private Wallet userWallet;
    private CompanyWallet companyWallet;
    private BigDecimal amount;
    private BigDecimal commissionAmount;
    private Commission commission;
    private OperationType operationType;
    private Currency currency;
    private Merchant merchant;
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    private LocalDateTime datetime;
    private ExOrder order;
    private boolean provided;
    private Integer confirmation;
    private BigDecimal activeBalanceBefore;
    private BigDecimal reservedBalanceBefore;
    private BigDecimal companyBalanceBefore;
    private BigDecimal companyCommissionBalanceBefore;
    private TransactionSourceType sourceType;
    private Integer sourceId;
    private String description;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Transaction that = (Transaction) o;

        if (id != that.id) return false;
        if (provided != that.provided) return false;
        if (activeBalanceBefore != null ? !activeBalanceBefore.equals(that.activeBalanceBefore) : that.activeBalanceBefore != null)
            return false;
        if (amount != null ? !amount.equals(that.amount) : that.amount != null) return false;
        if (commission != null ? !commission.equals(that.commission) : that.commission != null) return false;
        if (commissionAmount != null ? !commissionAmount.equals(that.commissionAmount) : that.commissionAmount != null)
            return false;
        if (companyBalanceBefore != null ? !companyBalanceBefore.equals(that.companyBalanceBefore) : that.companyBalanceBefore != null)
            return false;
        if (companyCommissionBalanceBefore != null ? !companyCommissionBalanceBefore.equals(that.companyCommissionBalanceBefore) : that.companyCommissionBalanceBefore != null)
            return false;
        if (companyWallet != null ? !companyWallet.equals(that.companyWallet) : that.companyWallet != null)
            return false;
        if (confirmation != null ? !confirmation.equals(that.confirmation) : that.confirmation != null) return false;
        if (currency != null ? !currency.equals(that.currency) : that.currency != null) return false;
        if (datetime != null ? !datetime.equals(that.datetime) : that.datetime != null) return false;
        if (merchant != null ? !merchant.equals(that.merchant) : that.merchant != null) return false;
        if (operationType != that.operationType) return false;
        if (order != null ? !order.equals(that.order) : that.order != null) return false;
        if (reservedBalanceBefore != null ? !reservedBalanceBefore.equals(that.reservedBalanceBefore) : that.reservedBalanceBefore != null)
            return false;
        if (sourceId != null ? !sourceId.equals(that.sourceId) : that.sourceId != null) return false;
        if (sourceType != that.sourceType) return false;
        if (userWallet != null ? !userWallet.equals(that.userWallet) : that.userWallet != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = id;
        result = 31 * result + (userWallet != null ? userWallet.hashCode() : 0);
        result = 31 * result + (companyWallet != null ? companyWallet.hashCode() : 0);
        result = 31 * result + (amount != null ? amount.hashCode() : 0);
        result = 31 * result + (commissionAmount != null ? commissionAmount.hashCode() : 0);
        result = 31 * result + (commission != null ? commission.hashCode() : 0);
        result = 31 * result + (operationType != null ? operationType.hashCode() : 0);
        result = 31 * result + (currency != null ? currency.hashCode() : 0);
        result = 31 * result + (merchant != null ? merchant.hashCode() : 0);
        result = 31 * result + (datetime != null ? datetime.hashCode() : 0);
        result = 31 * result + (order != null ? order.hashCode() : 0);
        result = 31 * result + (provided ? 1 : 0);
        result = 31 * result + (confirmation != null ? confirmation.hashCode() : 0);
        result = 31 * result + (activeBalanceBefore != null ? activeBalanceBefore.hashCode() : 0);
        result = 31 * result + (reservedBalanceBefore != null ? reservedBalanceBefore.hashCode() : 0);
        result = 31 * result + (companyBalanceBefore != null ? companyBalanceBefore.hashCode() : 0);
        result = 31 * result + (companyCommissionBalanceBefore != null ? companyCommissionBalanceBefore.hashCode() : 0);
        result = 31 * result + (sourceType != null ? sourceType.hashCode() : 0);
        result = 31 * result + (sourceId != null ? sourceId.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "Transaction{" +
                "id=" + id +
                ", userWallet=" + userWallet +
                ", companyWallet=" + companyWallet +
                ", amount=" + amount +
                ", commissionAmount=" + commissionAmount +
                ", commission=" + commission +
                ", operationType=" + operationType +
                ", currency=" + currency +
                ", merchant=" + merchant +
                ", datetime=" + datetime +
                ", order=" + order +
                ", provided=" + provided +
                ", confirmation=" + confirmation +
                ", activeBalanceBefore=" + activeBalanceBefore +
                ", reservedBalanceBefore=" + reservedBalanceBefore +
                ", companyBalanceBefore=" + companyBalanceBefore +
                ", companyCommissionBalanceBefore=" + companyCommissionBalanceBefore +
                ", sourceType=" + sourceType +
                ", sourceId=" + sourceId +
                '}';
    }
}