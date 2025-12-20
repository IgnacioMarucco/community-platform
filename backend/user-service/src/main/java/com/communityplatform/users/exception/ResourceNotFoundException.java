package com.communityplatform.users.exception;

/**
 * Exception thrown when a required resource is not found.
 */
public class ResourceNotFoundException extends BusinessException {

    public ResourceNotFoundException(String resourceName, String fieldName, Object fieldValue) {
        super(resourceName + " not found with " + fieldName + ": " + fieldValue);
    }

    public ResourceNotFoundException(String message) {
        super(message);
    }
}
