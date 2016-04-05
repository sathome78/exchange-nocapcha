package me.exrates.controller.exception;

/**
 * Created by Valk on 04.04.16.
 */
public class NotAcceptableOrderException extends RuntimeException {
    public NotAcceptableOrderException(String message) {
        super(message);
    }
}
