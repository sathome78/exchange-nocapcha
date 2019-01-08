package me.exrates.service.exception;

public class OrderCancellingException extends RuntimeException {

    public OrderCancellingException(String message) {
        super(message);
    }

    public OrderCancellingException(String message, Throwable cause) {
        super(message, cause);
    }

    public OrderCancellingException(Throwable cause) {
        super(cause);
    }
}
