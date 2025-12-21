package com.communityplatform.content.exception;

public class MediaNotFoundException extends RuntimeException {

    public MediaNotFoundException(Long mediaId) {
        super("Media not found: " + mediaId);
    }
}
