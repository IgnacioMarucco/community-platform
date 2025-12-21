package com.communityplatform.content.exception;

/**
 * Exception thrown for invalid like operations.
 * For example, duplicate likes or trying to unlike when not liked.
 */
public class InvalidLikeException extends RuntimeException {

    public InvalidLikeException(String message) {
        super(message);
    }

    public static InvalidLikeException alreadyLiked() {
        return new InvalidLikeException("You have already liked this content");
    }

    public static InvalidLikeException notLiked() {
        return new InvalidLikeException("You have not liked this content");
    }
}
