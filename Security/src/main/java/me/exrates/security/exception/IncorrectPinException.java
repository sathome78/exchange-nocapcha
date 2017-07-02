package me.exrates.security.exception;

/**
 * Created by maks on 02.07.2017.
 */
public class IncorrectPinException extends RuntimeException {

    public IncorrectPinException() {
    }

    public IncorrectPinException(String message) {
        super(message);
    }
}
