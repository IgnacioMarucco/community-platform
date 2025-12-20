package com.communityplatform.users.exception;

/**
 * Exception thrown when a token is invalid or malformed.
 */
public class InvalidTokenException extends BusinessException {

    public InvalidTokenException(String message) {
        super(message);
    }

    public InvalidTokenException() {
        super("The provided token is invalid");
    }
}
