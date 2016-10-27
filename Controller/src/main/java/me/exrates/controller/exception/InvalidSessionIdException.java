package me.exrates.controller.exception;

/**
 * Created by OLEG on 11.10.2016.
 */
public class InvalidSessionIdException extends RuntimeException {
    public InvalidSessionIdException() {
    }

    public InvalidSessionIdException(String message) {
        super(message);
    }

    public InvalidSessionIdException(String message, Throwable cause) {
        super(message, cause);
    }

    public InvalidSessionIdException(Throwable cause) {
        super(cause);
    }
}
