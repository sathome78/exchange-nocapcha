package me.exrates.controller.exception;

/**
 * Created by OLEG on 21.02.2017.
 */
public class InputRequestLimitExceededException extends RuntimeException {
    public InputRequestLimitExceededException() {
    }

    public InputRequestLimitExceededException(String message) {
        super(message);
    }

    public InputRequestLimitExceededException(String message, Throwable cause) {
        super(message, cause);
    }

    public InputRequestLimitExceededException(Throwable cause) {
        super(cause);
    }
}
