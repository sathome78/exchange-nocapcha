package me.exrates.model;

import me.exrates.model.enums.OperationType;

import java.math.BigDecimal;
import java.util.Optional;

/**
 * @author Denis Savin (pilgrimm333@gmail.com)
 */
public class CreditsOperation {

    private final User user;
    private final BigDecimal amount;
    private final BigDecimal commissionAmount;
    private final OperationType operationType;
    private final Commission commission;
    private final Currency currency;
    private final Merchant merchant;
    private final Optional<String> destination;

    private CreditsOperation(Builder builder) {
        this.user = builder.user;
        this.amount = builder.amount;
        this.commissionAmount = builder.commissionAmount;
        this.operationType = builder.operationType;
        this.commission = builder.commission;
        this.currency = builder.currency;
        this.merchant = builder.merchant;
        this.destination = builder.destination == null ?
                Optional.empty() : builder.destination;
    }

    public User getUser() {
        return user;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public BigDecimal getCommissionAmount() {
        return commissionAmount;
    }

    public OperationType getOperationType() {
        return operationType;
    }

    public Commission getCommission() {
        return commission;
    }

    public Currency getCurrency() {
        return currency;
    }

    public Merchant getMerchant() {
        return merchant;
    }

    public Optional<String> getDestination() {
        return destination;
    }

    public static class Builder {

        private User user;
        private BigDecimal amount;
        private BigDecimal commissionAmount;
        private OperationType operationType;
        private Commission commission;
        private Currency currency;
        private Merchant merchant;
        private Optional<String> destination;

        public Builder user(User user) {
            this.user = user;
            return this;
        }

        public Builder amount(BigDecimal amount) {
            this.amount = amount;
            return this;
        }

        public Builder commissionAmount(BigDecimal commissionAmount) {
            this.commissionAmount = commissionAmount;
            return this;
        }

        public Builder operationType(OperationType operationType) {
            this.operationType = operationType;
            return this;
        }

        public Builder commission(Commission commission) {
            this.commission = commission;
            return this;
        }

        public Builder currency(Currency currency) {
            this.currency = currency;
            return this;
        }

        public Builder merchant(Merchant merchant) {
            this.merchant = merchant;
            return this;
        }

        public Builder destination(String destination) {
            this.destination = Optional.ofNullable(destination);
            return this;
        }

        public CreditsOperation build() {
            return new CreditsOperation(this);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        CreditsOperation that = (CreditsOperation) o;

        if (user != null ? !user.equals(that.user) : that.user != null) return false;
        if (amount != null ? !amount.equals(that.amount) : that.amount != null) return false;
        if (commissionAmount != null ? !commissionAmount.equals(that.commissionAmount) : that.commissionAmount != null)
            return false;
        if (operationType != that.operationType) return false;
        if (commission != null ? !commission.equals(that.commission) : that.commission != null) return false;
        if (currency != null ? !currency.equals(that.currency) : that.currency != null) return false;
        if (merchant != null ? !merchant.equals(that.merchant) : that.merchant != null) return false;
        return destination != null ? destination.equals(that.destination) : that.destination == null;

    }

    @Override
    public int hashCode() {
        int result = user != null ? user.hashCode() : 0;
        result = 31 * result + (amount != null ? amount.hashCode() : 0);
        result = 31 * result + (commissionAmount != null ? commissionAmount.hashCode() : 0);
        result = 31 * result + (operationType != null ? operationType.hashCode() : 0);
        result = 31 * result + (commission != null ? commission.hashCode() : 0);
        result = 31 * result + (currency != null ? currency.hashCode() : 0);
        result = 31 * result + (merchant != null ? merchant.hashCode() : 0);
        result = 31 * result + (destination != null ? destination.hashCode() : 0);
        return result;
    }
}