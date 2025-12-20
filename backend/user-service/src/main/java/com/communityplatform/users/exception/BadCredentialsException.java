package com.communityplatform.users.exception;

/**
 * Exception thrown when authentication fails due to bad credentials.
 */
public class BadCredentialsException extends BusinessException {

    public BadCredentialsException(String message) {
        super(message);
    }

    public BadCredentialsException() {
        super("Invalid username/email or password");
    }
}
