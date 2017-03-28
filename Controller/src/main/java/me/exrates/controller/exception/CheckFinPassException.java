package me.exrates.controller.exception;

/**
 * Created by maks on 28.03.2017.
 */
public class CheckFinPassException extends RuntimeException {
    public CheckFinPassException() {
    }

    public CheckFinPassException(String message) {
        super(message);
    }
}
