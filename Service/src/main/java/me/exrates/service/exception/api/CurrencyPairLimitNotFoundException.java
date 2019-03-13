package me.exrates.service.exception.api;

public class CurrencyPairLimitNotFoundException extends RuntimeException {

    public CurrencyPairLimitNotFoundException(String message) {
        super(message);
    }

    public CurrencyPairLimitNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public CurrencyPairLimitNotFoundException(Throwable cause) {
        super(cause);
    }
}
