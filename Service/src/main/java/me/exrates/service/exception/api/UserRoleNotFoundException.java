package me.exrates.service.exception.api;

public class UserRoleNotFoundException extends RuntimeException {

    public UserRoleNotFoundException(String message) {
        super(message);
    }

    public UserRoleNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public UserRoleNotFoundException(Throwable cause) {
        super(cause);
    }
}
