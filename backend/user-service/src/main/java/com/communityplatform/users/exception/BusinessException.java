package com.communityplatform.users.exception;

/**
 * Base exception for all business logic exceptions in the application.
 * All custom exceptions should extend this class.
 */
public abstract class BusinessException extends RuntimeException {

    protected BusinessException(String message) {
        super(message);
    }

    protected BusinessException(String message, Throwable cause) {
        super(message, cause);
    }
}
