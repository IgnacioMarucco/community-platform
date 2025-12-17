package com.communityplatform.users.exception;

/**
 * Exception thrown when attempting to create a user with a username or email
 * that already exists.
 */
public class DuplicateUserException extends BusinessException {

    public DuplicateUserException(String field, String value) {
        super("User with " + field + " '" + value + "' already exists");
    }

    public DuplicateUserException(String message) {
        super(message);
    }
}
