package me.exrates.controller.exception;

/**
 * Created by OLEG on 05.09.2016.
 */
public class WrongOrderKeyException extends RuntimeException {
    public WrongOrderKeyException() {
    }

    public WrongOrderKeyException(String message) {
        super(message);
    }

    public WrongOrderKeyException(String message, Throwable cause) {
        super(message, cause);
    }

    public WrongOrderKeyException(Throwable cause) {
        super(cause);
    }
}
