package me.exrates.controller.exception;

/**
 * Created by OLEG on 09.02.2017.
 */
public class InvoiceNotFoundException extends RuntimeException {

    public InvoiceNotFoundException() {
    }

    public InvoiceNotFoundException(String message) {
        super(message);
    }

    public InvoiceNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public InvoiceNotFoundException(Throwable cause) {
        super(cause);
    }
}
