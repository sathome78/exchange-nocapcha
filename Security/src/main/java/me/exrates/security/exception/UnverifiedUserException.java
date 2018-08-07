package me.exrates.security.exception;

import org.springframework.security.core.AuthenticationException;

/**
 * @author Vlad Dziubak
 * Date: 07.08.2018
 */
public class UnverifiedUserException extends AuthenticationException {

    public UnverifiedUserException(String msg, Throwable t) {
        super(msg, t);
    }

    public UnverifiedUserException(String msg) {
        super(msg);
    }
}
