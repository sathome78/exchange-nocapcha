package me.exrates.model.ngExceptions;

public class NgCurrencyNotFoundException extends RuntimeException {
    public NgCurrencyNotFoundException(String message) {
        super(message);
    }
}
