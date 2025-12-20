package com.communityplatform.users.exception;

/**
 * Exception thrown when attempting to follow a user that is already being
 * followed.
 */
public class AlreadyFollowingException extends BusinessException {

    public AlreadyFollowingException(Long userId) {
        super("Already following user with id: " + userId);
    }

    public AlreadyFollowingException(String message) {
        super(message);
    }
}
