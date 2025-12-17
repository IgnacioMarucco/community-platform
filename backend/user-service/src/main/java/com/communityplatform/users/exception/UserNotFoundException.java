package com.communityplatform.users.exception;

/**
 * Exception thrown when a user is not found by ID.
 */
public class UserNotFoundException extends BusinessException {

    public UserNotFoundException(Long id) {
        super("User not found with id: " + id);
    }

    public UserNotFoundException(String message) {
        super(message);
    }
}
