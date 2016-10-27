package me.exrates.controller.exception;

/**
 * Created by OLEG on 12.10.2016.
 */
public class NotSupportedLanguageException extends RuntimeException {
    public NotSupportedLanguageException() {
    }

    public NotSupportedLanguageException(String message) {
        super(message);
    }

    public NotSupportedLanguageException(String message, Throwable cause) {
        super(message, cause);
    }

    public NotSupportedLanguageException(Throwable cause) {
        super(cause);
    }
}
