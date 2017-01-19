package me.exrates.model.enums;

/**
 * Created by OLEG on 20.09.2016.
 */
import static me.exrates.model.enums.TransactionSourceType.*;
public enum TransactionType {
    INPUT(MERCHANT, OperationType.INPUT),
    OUTPUT(MERCHANT, OperationType.OUTPUT),
    ORDER_IN(ORDER, OperationType.INPUT),
    ORDER_OUT(ORDER, OperationType.OUTPUT),
    WALLET_INNER_TRANSFER(null, OperationType.WALLET_INNER_TRANSFER),
    REFERRAL(TransactionSourceType.REFERRAL, null),
    MANUAL(TransactionSourceType.MANUAL, null),
    USER_TRANSFER_IN(USER_TRANSFER, OperationType.INPUT),
    USER_TRANSFER_OUT(USER_TRANSFER, OperationType.OUTPUT);

    private TransactionSourceType sourceType;
    private OperationType operationType;

    TransactionType(TransactionSourceType sourceType, OperationType operationType) {
        this.sourceType = sourceType;
        this.operationType = operationType;
    }

    public TransactionSourceType getSourceType() {
        return sourceType;
    }

    public void setSourceType(TransactionSourceType sourceType) {
        this.sourceType = sourceType;
    }

    public OperationType getOperationType() {
        return operationType;
    }

    public void setOperationType(OperationType operationType) {
        this.operationType = operationType;
    }

    public static TransactionType resolveFromOperationTypeAndSource(TransactionSourceType sourceType, OperationType operationType) {
        if (sourceType == MERCHANT && operationType == OperationType.INPUT) return INPUT;
        if (sourceType == MERCHANT && operationType == OperationType.OUTPUT) return OUTPUT;
        if (sourceType == ORDER && operationType == OperationType.INPUT) return ORDER_IN;
        if (sourceType == ORDER && operationType == OperationType.OUTPUT) return ORDER_OUT;
        if (sourceType == USER_TRANSFER && operationType == OperationType.INPUT) return USER_TRANSFER_IN;
        if (sourceType == USER_TRANSFER && operationType == OperationType.OUTPUT) return USER_TRANSFER_OUT;
        return TransactionType.valueOf(operationType.name());

    }
}
