package me.exrates.service.exception;

/**
 * Created by OLEG on 23.11.2016.
 */
public class ManualBalanceChangeException extends RuntimeException {
    public ManualBalanceChangeException() {
    }

    public ManualBalanceChangeException(String message) {
        super(message);
    }

    public ManualBalanceChangeException(String message, Throwable cause) {
        super(message, cause);
    }

    public ManualBalanceChangeException(Throwable cause) {
        super(cause);
    }
}
