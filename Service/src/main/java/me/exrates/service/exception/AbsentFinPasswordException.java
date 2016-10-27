package me.exrates.service.exception;

/**
 * Created by Valk on 04.04.16.
 */
public class AbsentFinPasswordException extends RuntimeException {
    public AbsentFinPasswordException(String message) {
        super(message);
    }
}
