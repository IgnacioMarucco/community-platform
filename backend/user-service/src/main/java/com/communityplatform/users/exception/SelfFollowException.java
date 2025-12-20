package com.communityplatform.users.exception;

/**
 * Exception thrown when a user attempts to follow themselves.
 */
public class SelfFollowException extends BusinessException {

    public SelfFollowException() {
        super("Users cannot follow themselves");
    }

    public SelfFollowException(String message) {
        super(message);
    }
}
