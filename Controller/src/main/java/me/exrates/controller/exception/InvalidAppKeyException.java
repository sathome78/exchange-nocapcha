package me.exrates.controller.exception;

/**
 * Created by OLEG on 07.10.2016.
 */
public class InvalidAppKeyException extends RuntimeException {
    public InvalidAppKeyException() {
    }

    public InvalidAppKeyException(String message) {
        super(message);
    }

    public InvalidAppKeyException(String message, Throwable cause) {
        super(message, cause);
    }

    public InvalidAppKeyException(Throwable cause) {
        super(cause);
    }
}
