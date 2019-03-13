package me.exrates.service.exception.api;

public class CommissionsNotFoundException extends RuntimeException {

    public CommissionsNotFoundException(String message) {
        super(message);
    }

    public CommissionsNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public CommissionsNotFoundException(Throwable cause) {
        super(cause);
    }
}
