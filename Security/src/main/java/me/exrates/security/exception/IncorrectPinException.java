package me.exrates.security.exception;

import org.springframework.security.core.AuthenticationException;

/**
 * Created by maks on 02.07.2017.
 */
public class IncorrectPinException extends AuthenticationException {

    public IncorrectPinException(String message) {
        super(message);
    }
}
