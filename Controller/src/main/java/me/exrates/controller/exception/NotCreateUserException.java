package me.exrates.controller.exception;

/**
 * Created by Valk on 04.04.16.
 */
public class NotCreateUserException extends RuntimeException {
    public NotCreateUserException(String message) {
        super(message);
    }
}
