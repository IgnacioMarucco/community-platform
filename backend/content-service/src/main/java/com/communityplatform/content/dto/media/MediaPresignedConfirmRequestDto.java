package com.communityplatform.content.dto.media;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Request to confirm a presigned upload and register metadata.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MediaPresignedConfirmRequestDto {

    @NotBlank(message = "Stored filename is required")
    private String storedFilename;

    @NotBlank(message = "Original filename is required")
    private String originalFilename;

    private String mimeType;

    @Positive(message = "File size must be positive")
    private Long fileSize;
}
