package com.communityplatform.content.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.communityplatform.content.dto.media.MediaPresignedConfirmRequestDto;
import com.communityplatform.content.dto.media.MediaPresignedDownloadResponseDto;
import com.communityplatform.content.dto.media.MediaPresignedUploadRequestDto;
import com.communityplatform.content.dto.media.MediaPresignedUploadResponseDto;
import com.communityplatform.content.dto.media.MediaResponseDto;
import com.communityplatform.content.dto.media.MediaUploadResponseDto;
import com.communityplatform.content.service.MediaService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * REST Controller for Media management.
 */
@RestController
@RequestMapping("${api.base-path}/media")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Media", description = "Media upload and metadata endpoints")
public class MediaController {

    private final MediaService mediaService;

    @Operation(summary = "Upload media")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Media uploaded successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid file")
    })
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<MediaUploadResponseDto> uploadMedia(
            @RequestHeader("X-User-Id") Long userId,
            @RequestPart("file") MultipartFile file) {
        log.info("Uploading media for user: {}", userId);
        MediaUploadResponseDto response = mediaService.uploadMedia(file, userId);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(summary = "Create presigned upload URL")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Presigned upload URL created"),
            @ApiResponse(responseCode = "400", description = "Invalid request")
    })
    @PostMapping("/presigned/upload")
    public ResponseEntity<MediaPresignedUploadResponseDto> createPresignedUpload(
            @RequestHeader("X-User-Id") Long userId,
            @Valid @RequestBody MediaPresignedUploadRequestDto request) {
        log.info("Creating presigned upload URL for user: {}", userId);
        MediaPresignedUploadResponseDto response = mediaService.createPresignedUpload(request, userId);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Confirm presigned upload and register metadata")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Media registered successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request")
    })
    @PostMapping("/presigned/confirm")
    public ResponseEntity<MediaUploadResponseDto> confirmPresignedUpload(
            @RequestHeader("X-User-Id") Long userId,
            @Valid @RequestBody MediaPresignedConfirmRequestDto request) {
        log.info("Confirming presigned upload for user: {}", userId);
        MediaUploadResponseDto response = mediaService.confirmPresignedUpload(request, userId);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(summary = "Get media metadata by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Media retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "Media not found")
    })
    @GetMapping("/{mediaId}")
    public ResponseEntity<MediaResponseDto> getMediaById(@PathVariable Long mediaId) {
        log.info("Getting media: {}", mediaId);
        MediaResponseDto response = mediaService.getMediaById(mediaId);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Create presigned download URL")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Presigned download URL created"),
            @ApiResponse(responseCode = "404", description = "Media not found")
    })
    @GetMapping("/{mediaId}/presigned-download")
    public ResponseEntity<MediaPresignedDownloadResponseDto> createPresignedDownload(
            @PathVariable Long mediaId) {
        log.info("Creating presigned download URL for media: {}", mediaId);
        MediaPresignedDownloadResponseDto response = mediaService.createPresignedDownload(mediaId);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Get media uploaded by user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Media retrieved successfully")
    })
    @GetMapping("/user/{userId}")
    public ResponseEntity<Page<MediaResponseDto>> getMediaByUserId(
            @PathVariable Long userId,
            @PageableDefault(size = 20) Pageable pageable) {
        log.info("Getting media for user: {}", userId);
        Page<MediaResponseDto> response = mediaService.getMediaByUserId(userId, pageable);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Delete media")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Media deleted successfully"),
            @ApiResponse(responseCode = "403", description = "Unauthorized to delete"),
            @ApiResponse(responseCode = "404", description = "Media not found")
    })
    @DeleteMapping("/{mediaId}")
    public ResponseEntity<Void> deleteMedia(
            @PathVariable Long mediaId,
            @RequestHeader("X-User-Id") Long userId) {
        log.info("Deleting media: {}", mediaId);
        mediaService.deleteMedia(mediaId, userId);
        return ResponseEntity.noContent().build();
    }
}
