package me.exrates.service.exception.invoice;

/**
 * Created by Valk
 */
public class IllegalInvoiceRequestStatusException extends Exception {

    public IllegalInvoiceRequestStatusException(String message) {
        super(message);
    }

    public IllegalInvoiceRequestStatusException(String message, Throwable cause) {
        super(message, cause);
    }
}
