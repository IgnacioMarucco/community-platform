package com.communityplatform.content.dto.media;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Response for presigned download URL creation.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MediaPresignedDownloadResponseDto {

    private String downloadUrl;
    private Long expiresInSeconds;
}
