package me.exrates.service.exception;

public class WavesRestException extends RuntimeException {
    public WavesRestException() {
    }

    public WavesRestException(String message) {
        super(message);
    }

    public WavesRestException(String message, Throwable cause) {
        super(message, cause);
    }

    public WavesRestException(Throwable cause) {
        super(cause);
    }
}
