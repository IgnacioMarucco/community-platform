package com.communityplatform.content.exception;

/**
 * Exception thrown when a user attempts an unauthorized operation.
 * For example, deleting someone else's post or comment.
 */
public class UnauthorizedOperationException extends RuntimeException {

    public UnauthorizedOperationException(String message) {
        super(message);
    }

    public UnauthorizedOperationException() {
        super("You are not authorized to perform this operation");
    }
}
