package me.exrates.controller.exception;

/**
 * Created by OLEG on 05.10.2016.
 */
public class InvalidFileException extends RuntimeException {
    public InvalidFileException() {
    }

    public InvalidFileException(String message) {
        super(message);
    }

    public InvalidFileException(String message, Throwable cause) {
        super(message, cause);
    }

    public InvalidFileException(Throwable cause) {
        super(cause);
    }
}
