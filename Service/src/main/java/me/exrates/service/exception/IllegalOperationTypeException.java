package me.exrates.service.exception;

/**
 * Created by Valk
 */
public class IllegalOperationTypeException extends Exception {

    public IllegalOperationTypeException(String message) {
        super(message);
    }

    public IllegalOperationTypeException(String message, Throwable cause) {
        super(message, cause);
    }
}
