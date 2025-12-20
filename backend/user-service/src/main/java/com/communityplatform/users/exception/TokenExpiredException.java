package com.communityplatform.users.exception;

/**
 * Exception thrown when a token has expired.
 */
public class TokenExpiredException extends BusinessException {

    public TokenExpiredException(String message) {
        super(message);
    }

    public TokenExpiredException() {
        super("The provided token has expired");
    }
}
