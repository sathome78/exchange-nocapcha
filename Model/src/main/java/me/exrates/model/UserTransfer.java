package me.exrates.model;

import java.math.BigDecimal;

/**
 * Created by OLEG on 17.01.2017.
 */
public class UserTransfer {
    private Integer fromUserId;
    private Integer toUserId;
    private Integer currencyId;
    private BigDecimal amount;
    private BigDecimal commissionAmount;

    public Integer getFromUserId() {
        return fromUserId;
    }

    public void setFromUserId(Integer fromUserId) {
        this.fromUserId = fromUserId;
    }

    public Integer getToUserId() {
        return toUserId;
    }

    public void setToUserId(Integer toUserId) {
        this.toUserId = toUserId;
    }

    public Integer getCurrencyId() {
        return currencyId;
    }

    public void setCurrencyId(Integer currencyId) {
        this.currencyId = currencyId;
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

    @Override
    public String toString() {
        return "UserTransfer{" +
                "fromUserId=" + fromUserId +
                ", toUserId=" + toUserId +
                ", currencyId=" + currencyId +
                ", amount=" + amount +
                ", commissionAmount=" + commissionAmount +
                '}';
    }
}
