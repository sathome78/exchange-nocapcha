package me.exrates.controller.exception;

/**
 * Created by Valk on 04.04.16.
 */
public class ErrorInfo {
    public final String url;
    public final String cause;
    public final String detail;

    public ErrorInfo(CharSequence url, Throwable ex) {
        this.url = url.toString();
        this.cause = ex.getClass().getSimpleName();
        String detail = ex.getLocalizedMessage();
        while (ex.getCause() != null) ex = ex.getCause();
        this.detail = ex.getLocalizedMessage() == null ? detail : ex.getLocalizedMessage();
    }

    public ErrorInfo(CharSequence url, Throwable ex, String reason) {
        this.url = url.toString();
        this.cause = ex.getClass().getSimpleName();
        while (ex.getCause() != null) ex = ex.getCause();
        this.detail = reason;
    }
}
