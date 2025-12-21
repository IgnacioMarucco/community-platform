package com.communityplatform.content.dto.media;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO returned after successful media upload.
 * Contains minimal info needed by client.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MediaUploadResponseDto {

    /**
     * Media ID.
     */
    private Long id;

    /**
     * Original filename.
     */
    private String originalFilename;

    /**
     * Stored filename in MinIO.
     */
    private String storedFilename;

    /**
     * Full URL to access the file.
     */
    private String url;

    /**
     * File size in bytes.
     */
    private Long fileSize;

    /**
     * MIME type.
     */
    private String mimeType;

    /**
     * Upload timestamp.
     */
    private LocalDateTime createdAt;
}
