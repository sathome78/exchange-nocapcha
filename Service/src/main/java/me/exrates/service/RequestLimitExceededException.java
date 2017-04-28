package me.exrates.service;

/**
 * Created by ajet on 13.04.2017.
 */
public class RequestLimitExceededException extends RuntimeException {

    public RequestLimitExceededException(String message) {
        super(message);
    }

}
