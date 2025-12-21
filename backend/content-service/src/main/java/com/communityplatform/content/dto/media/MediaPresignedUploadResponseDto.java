package com.communityplatform.content.dto.media;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Response for presigned upload URL creation.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MediaPresignedUploadResponseDto {

    private String uploadUrl;
    private String bucketName;
    private String objectKey;
    private Long expiresInSeconds;
    private String objectUrl;
}
