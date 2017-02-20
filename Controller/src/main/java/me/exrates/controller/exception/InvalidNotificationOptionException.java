package me.exrates.controller.exception;

/**
 * Created by OLEG on 27.01.2017.
 */
public class InvalidNotificationOptionException extends RuntimeException {
    public InvalidNotificationOptionException() {
    }

    public InvalidNotificationOptionException(String message) {
        super(message);
    }

    public InvalidNotificationOptionException(String message, Throwable cause) {
        super(message, cause);
    }

    public InvalidNotificationOptionException(Throwable cause) {
        super(cause);
    }
}
