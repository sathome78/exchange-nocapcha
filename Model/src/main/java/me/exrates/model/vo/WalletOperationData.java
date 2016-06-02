package me.exrates.model.vo;

import me.exrates.model.Commission;
import me.exrates.model.Transaction;
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
    private BigDecimal commissionAmount;
    private TransactionSourceType sourceType;
    private int sourceId;
    private Transaction transaction;

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

    public BigDecimal getCommissionAmount() {
        return commissionAmount;
    }

    public void setCommissionAmount(BigDecimal commissionAmount) {
        this.commissionAmount = commissionAmount;
    }

    public BalanceType getBalanceType() {
        return balanceType;
    }

    public void setBalanceType(BalanceType balanceType) {
        this.balanceType = balanceType;
    }

    public Transaction getTransaction() {
        return transaction;
    }

    public void setTransaction(final Transaction transaction) {
        this.transaction = transaction;
    }

    /**/
    public enum BalanceType {
        ACTIVE,
        RESERVED
    }
}
