package com.communityplatform.content.exception;

/**
 * Exception thrown when a comment is not found.
 */
public class CommentNotFoundException extends RuntimeException {

    public CommentNotFoundException(Long commentId) {
        super("Comment not found with id: " + commentId);
    }

    public CommentNotFoundException(String message) {
        super(message);
    }
}
