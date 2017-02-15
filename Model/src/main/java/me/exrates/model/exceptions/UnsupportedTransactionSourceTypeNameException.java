package me.exrates.model.exceptions;

public class UnsupportedTransactionSourceTypeException extends RuntimeException {

    public UnsupportedTransactionSourceTypeException(String chartType) {
        super("No such transaction source type " + chartType);
    }
}