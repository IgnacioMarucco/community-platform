package com.communityplatform.users.exception;

/**
 * Exception thrown when attempting to unfollow a user that is not being
 * followed.
 */
public class NotFollowingException extends BusinessException {

    public NotFollowingException(Long userId) {
        super("Not following user with id: " + userId);
    }

    public NotFollowingException(String message) {
        super(message);
    }
}
