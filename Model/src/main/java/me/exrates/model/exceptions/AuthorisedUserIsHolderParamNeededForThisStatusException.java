package me.exrates.model.exceptions;

/**
 * Created by ValkSam
 */
public class AuthorisedUserIsHolderParamNeededForThisStatusException extends RuntimeException {
    public AuthorisedUserIsHolderParamNeededForThisStatusException(String message) {
        super(message);
    }
}
