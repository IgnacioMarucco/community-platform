package com.communityplatform.content.exception;

/**
 * Exception thrown when a post is not found.
 */
public class PostNotFoundException extends RuntimeException {

    public PostNotFoundException(Long postId) {
        super("Post not found with id: " + postId);
    }

    public PostNotFoundException(String message) {
        super(message);
    }
}
