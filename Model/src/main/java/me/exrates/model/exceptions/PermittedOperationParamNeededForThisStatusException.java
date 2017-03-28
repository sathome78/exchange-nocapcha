package me.exrates.model.exceptions;

/**
 * Created by ValkSam
 */
public class PermittedOperationParamNeededForThisStatusException extends RuntimeException {
    public PermittedOperationParamNeededForThisStatusException(String message) {
        super(message);
    }
}
