package me.exrates.model.vo;

import me.exrates.model.Commission;
import me.exrates.model.enums.OperationType;
import me.exrates.model.enums.TransactionSourceType;

import java.math.BigDecimal;

/**
 * Created by Valk on 24.05.2016.
 */
public class WalletOperationData {
    private OperationType operationType;
    private int walletId;
    private BigDecimal amount;
    private BalanceType balanceType;
    private Commission commission;
    private BigDecimal commmissionAmount;
    private TransactionSourceType sourceType;
    private int sourceId;

    public int getWalletId() {
        return walletId;
    }
    /*getters setters*/

    public void setWalletId(int walletId) {
        this.walletId = walletId;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public OperationType getOperationType() {
        return operationType;
    }

    public void setOperationType(OperationType operationType) {
        this.operationType = operationType;
    }

    public TransactionSourceType getSourceType() {
        return sourceType;
    }

    public void setSourceType(TransactionSourceType sourceType) {
        this.sourceType = sourceType;
    }

    public int getSourceId() {
        return sourceId;
    }

    public void setSourceId(int sourceId) {
        this.sourceId = sourceId;
    }

    public Commission getCommission() {
        return commission;
    }

    public void setCommission(Commission commission) {
        this.commission = commission;
    }

    public BigDecimal getCommmissionAmount() {
        return commmissionAmount;
    }

    public void setCommmissionAmount(BigDecimal commmissionAmount) {
        this.commmissionAmount = commmissionAmount;
    }

    public BalanceType getBalanceType() {
        return balanceType;
    }

    public void setBalanceType(BalanceType balanceType) {
        this.balanceType = balanceType;
    }

    /**/
    public static enum BalanceType {
        ACTIVE,
        RESERVED
    }

}
