package com.communityplatform.content.dto.media;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for complete media metadata.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MediaResponseDto {

    /**
     * Media ID.
     */
    private Long id;

    /**
     * Original filename.
     */
    private String originalFilename;

    /**
     * Stored filename.
     */
    private String storedFilename;

    /**
     * MIME type.
     */
    private String mimeType;

    /**
     * File size in bytes.
     */
    private Long fileSize;

    /**
     * MinIO bucket name.
     */
    private String bucketName;

    /**
     * Uploader user ID.
     */
    private Long uploaderUserId;

    /**
     * Full URL.
     */
    private String url;

    /**
     * Upload timestamp.
     */
    private LocalDateTime createdAt;
}
