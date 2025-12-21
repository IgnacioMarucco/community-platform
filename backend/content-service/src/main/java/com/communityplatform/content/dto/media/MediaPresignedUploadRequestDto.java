package com.communityplatform.content.dto.media;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Request for creating a presigned upload URL.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MediaPresignedUploadRequestDto {

    @NotBlank(message = "Original filename is required")
    private String originalFilename;

    private String mimeType;

    @Positive(message = "File size must be positive")
    private Long fileSize;
}
