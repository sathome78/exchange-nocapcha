package me.exrates.model;

import me.exrates.model.enums.OperationType;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * @author Denis Savin (pilgrimm333@gmail.com)
 */
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
    private LocalDateTime datetime;
    private boolean provided;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Wallet getUserWallet() {
        return userWallet;
    }

    public void setUserWallet(Wallet userWallet) {
        this.userWallet = userWallet;
    }

    public CompanyWallet getCompanyWallet() {
        return companyWallet;
    }

    public void setCompanyWallet(CompanyWallet companyWallet) {
        this.companyWallet = companyWallet;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public BigDecimal getCommissionAmount() {
        return commissionAmount;
    }

    public void setCommissionAmount(BigDecimal commissionAmount) {
        this.commissionAmount = commissionAmount;
    }

    public Commission getCommission() {
        return commission;
    }

    public void setCommission(Commission commission) {
        this.commission = commission;
    }

    public OperationType getOperationType() {
        return operationType;
    }

    public void setOperationType(OperationType operationType) {
        this.operationType = operationType;
    }

    public Currency getCurrency() {
        return currency;
    }

    public void setCurrency(Currency currency) {
        this.currency = currency;
    }

    public Merchant getMerchant() {
        return merchant;
    }

    public void setMerchant(Merchant merchant) {
        this.merchant = merchant;
    }

    public LocalDateTime getDatetime() {
        return datetime;
    }

    public void setDatetime(LocalDateTime datetime) {
        this.datetime = datetime;
    }

    public boolean isProvided() {
        return provided;
    }

    public void setProvided(boolean provided) {
        this.provided = provided;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Transaction that = (Transaction) o;

        if (id != that.id) return false;
        if (provided != that.provided) return false;
        if (userWallet != null ? !userWallet.equals(that.userWallet) : that.userWallet != null) return false;
        if (companyWallet != null ? !companyWallet.equals(that.companyWallet) : that.companyWallet != null)
            return false;
        if (amount != null ? !amount.equals(that.amount) : that.amount != null) return false;
        if (commissionAmount != null ? !commissionAmount.equals(that.commissionAmount) : that.commissionAmount != null)
            return false;
        if (commission != null ? !commission.equals(that.commission) : that.commission != null) return false;
        if (operationType != that.operationType) return false;
        if (currency != null ? !currency.equals(that.currency) : that.currency != null) return false;
        if (merchant != null ? !merchant.equals(that.merchant) : that.merchant != null) return false;
        return datetime != null ? datetime.equals(that.datetime) : that.datetime == null;

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
        result = 31 * result + (provided ? 1 : 0);
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
                ", provided=" + provided +
                '}';
    }
}