package me.exrates.controller.exception;

public class WrongUsernameOrPasswordException extends RuntimeException {
    public WrongUsernameOrPasswordException() {
    }

    public WrongUsernameOrPasswordException(String message) {
        super(message);
    }

    public WrongUsernameOrPasswordException(String message, Throwable cause) {
        super(message, cause);
    }

    public WrongUsernameOrPasswordException(Throwable cause) {
        super(cause);
    }
}
