package me.exrates.controller.exception;

/**
 * Created by Valk on 04.04.16.
 */
public class WrongFinPasswordException extends RuntimeException {
    public WrongFinPasswordException(String message) {
        super(message);
    }
}
