package com.communityplatform.content.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import com.communityplatform.content.dto.media.MediaPresignedDownloadResponseDto;
import com.communityplatform.content.dto.media.MediaPresignedUploadRequestDto;
import com.communityplatform.content.dto.media.MediaPresignedUploadResponseDto;
import com.communityplatform.content.dto.media.MediaPresignedConfirmRequestDto;
import com.communityplatform.content.dto.media.MediaResponseDto;
import com.communityplatform.content.dto.media.MediaUploadResponseDto;

/**
 * Service interface for Media operations.
 */
public interface MediaService {

    /**
     * Upload a media file and store metadata.
     */
    MediaUploadResponseDto uploadMedia(MultipartFile file, Long uploaderUserId);

    /**
     * Get media metadata by ID.
     */
    MediaResponseDto getMediaById(Long mediaId);

    /**
     * List media uploaded by a user.
     */
    Page<MediaResponseDto> getMediaByUserId(Long uploaderUserId, Pageable pageable);

    /**
     * Create a presigned upload URL for direct MinIO upload.
     */
    MediaPresignedUploadResponseDto createPresignedUpload(MediaPresignedUploadRequestDto request, Long uploaderUserId);

    /**
     * Confirm a presigned upload and persist metadata.
     */
    MediaUploadResponseDto confirmPresignedUpload(MediaPresignedConfirmRequestDto request, Long uploaderUserId);

    /**
     * Create a presigned download URL for a stored media.
     */
    MediaPresignedDownloadResponseDto createPresignedDownload(Long mediaId);

    /**
     * Delete media by ID (owner only).
     */
    void deleteMedia(Long mediaId, Long requesterUserId);
}
