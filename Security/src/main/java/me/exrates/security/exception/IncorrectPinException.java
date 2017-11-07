package me.exrates.security.exception;

import org.springframework.security.core.AuthenticationException;

/**
 * Created by maks on 02.07.2017.
 */
public class IncorrectPinException extends AuthenticationException {

    private final String REASON_CODE = "message.pin_code.incorrect";

    public IncorrectPinException(String message) {
        super(message);
    }

    public String getReason() {
        return REASON_CODE;
    }
}
