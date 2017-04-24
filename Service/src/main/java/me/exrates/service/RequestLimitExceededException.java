package me.exrates.service;

/**
 * Created by ajet on 13.04.2017.
 */
public class RequestLimitExceededException extends RuntimeException {

    public RequestLimitExceededException() {
    }

    public RequestLimitExceededException(String message) {
        super(message);
    }

    public RequestLimitExceededException(String message, Throwable cause) {
        super(message, cause);
    }

    public RequestLimitExceededException(Throwable cause) {
        super(cause);
    }
}
