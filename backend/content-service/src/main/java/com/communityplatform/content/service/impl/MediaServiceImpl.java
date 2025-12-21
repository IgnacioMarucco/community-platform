package com.communityplatform.content.service.impl;

import java.io.InputStream;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import com.communityplatform.content.dto.media.MediaPresignedConfirmRequestDto;
import com.communityplatform.content.dto.media.MediaPresignedDownloadResponseDto;
import com.communityplatform.content.dto.media.MediaPresignedUploadRequestDto;
import com.communityplatform.content.dto.media.MediaPresignedUploadResponseDto;
import com.communityplatform.content.dto.media.MediaResponseDto;
import com.communityplatform.content.dto.media.MediaUploadResponseDto;
import com.communityplatform.content.entity.MediaEntity;
import com.communityplatform.content.exception.InvalidMediaException;
import com.communityplatform.content.exception.MediaNotFoundException;
import com.communityplatform.content.exception.MediaStorageException;
import com.communityplatform.content.exception.UnauthorizedOperationException;
import com.communityplatform.content.mapper.MediaMapper;
import com.communityplatform.content.repository.MediaRepository;
import com.communityplatform.content.service.MediaService;

import io.minio.GetPresignedObjectUrlArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.RemoveObjectArgs;
import io.minio.StatObjectArgs;
import io.minio.StatObjectResponse;
import io.minio.http.Method;
import lombok.extern.slf4j.Slf4j;

/**
 * Implementation of MediaService using MinIO for storage.
 */
@Service
@Slf4j
@Transactional
public class MediaServiceImpl implements MediaService {

    private static final long MIN_MULTIPART_SIZE = 5 * 1024 * 1024;

    private final MediaRepository mediaRepository;
    private final MediaMapper mediaMapper;
    private final MinioClient minioClient;
    private final MinioClient presignedMinioClient;

    public MediaServiceImpl(MediaRepository mediaRepository,
            MediaMapper mediaMapper,
            MinioClient minioClient,
            @org.springframework.beans.factory.annotation.Qualifier("presignedMinioClient") MinioClient presignedMinioClient) {
        this.mediaRepository = mediaRepository;
        this.mediaMapper = mediaMapper;
        this.minioClient = minioClient;
        this.presignedMinioClient = presignedMinioClient;
    }

    @Value("${minio.bucket-name}")
    private String bucketName;

    @Value("${minio.public-url:${minio.url}}")
    private String publicUrl;

    @Value("${minio.presigned-expiry-seconds:900}")
    private int presignedExpirySeconds;

    @Override
    public MediaUploadResponseDto uploadMedia(MultipartFile file, Long uploaderUserId) {
        if (uploaderUserId == null) {
            throw new InvalidMediaException("Uploader user id is required");
        }
        if (file == null || file.isEmpty()) {
            throw new InvalidMediaException("File is required");
        }

        String originalFilename = StringUtils.cleanPath(
                Optional.ofNullable(file.getOriginalFilename()).orElse("upload"));
        String storedFilename = createStoredFilename(originalFilename);
        String contentType = Optional.ofNullable(file.getContentType())
                .orElse("application/octet-stream");

        try (InputStream inputStream = file.getInputStream()) {
            long partSize = Math.max(file.getSize(), MIN_MULTIPART_SIZE);
            minioClient.putObject(
                    PutObjectArgs.builder()
                            .bucket(bucketName)
                            .object(storedFilename)
                            .stream(inputStream, file.getSize(), partSize)
                            .contentType(contentType)
                            .build());
        } catch (Exception ex) {
            log.error("Failed to upload media to MinIO", ex);
            throw new MediaStorageException("Failed to store media file", ex);
        }

        String url = buildPublicUrl(bucketName, storedFilename);
        MediaEntity entity = MediaEntity.builder()
                .originalFilename(originalFilename)
                .storedFilename(storedFilename)
                .mimeType(contentType)
                .fileSize(file.getSize())
                .bucketName(bucketName)
                .uploaderUserId(uploaderUserId)
                .url(url)
                .build();

        MediaEntity saved = mediaRepository.save(entity);
        return mediaMapper.toUploadResponseDto(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public MediaResponseDto getMediaById(Long mediaId) {
        MediaEntity entity = mediaRepository.findById(mediaId)
                .orElseThrow(() -> new MediaNotFoundException(mediaId));
        return mediaMapper.toResponseDto(entity);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<MediaResponseDto> getMediaByUserId(Long uploaderUserId, Pageable pageable) {
        return mediaRepository.findByUploaderUserId(uploaderUserId, pageable)
                .map(mediaMapper::toResponseDto);
    }

    @Override
    public MediaPresignedUploadResponseDto createPresignedUpload(MediaPresignedUploadRequestDto request,
            Long uploaderUserId) {
        if (uploaderUserId == null) {
            throw new InvalidMediaException("Uploader user id is required");
        }
        if (request == null || !StringUtils.hasText(request.getOriginalFilename())) {
            throw new InvalidMediaException("Original filename is required");
        }

        String originalFilename = StringUtils.cleanPath(request.getOriginalFilename());
        String storedFilename = createStoredFilename(originalFilename);

        try {
            String uploadUrl = presignedMinioClient.getPresignedObjectUrl(
                    GetPresignedObjectUrlArgs.builder()
                            .method(Method.PUT)
                            .bucket(bucketName)
                            .object(storedFilename)
                            .expiry(presignedExpirySeconds)
                            .build());

            return MediaPresignedUploadResponseDto.builder()
                    .uploadUrl(uploadUrl)
                    .bucketName(bucketName)
                    .objectKey(storedFilename)
                    .expiresInSeconds((long) presignedExpirySeconds)
                    .objectUrl(buildPublicUrl(bucketName, storedFilename))
                    .build();
        } catch (Exception ex) {
            log.error("Failed to create presigned upload URL", ex);
            throw new MediaStorageException("Failed to create presigned upload URL", ex);
        }
    }

    @Override
    public MediaUploadResponseDto confirmPresignedUpload(MediaPresignedConfirmRequestDto request,
            Long uploaderUserId) {
        if (uploaderUserId == null) {
            throw new InvalidMediaException("Uploader user id is required");
        }
        if (request == null || !StringUtils.hasText(request.getStoredFilename())
                || !StringUtils.hasText(request.getOriginalFilename())) {
            throw new InvalidMediaException("Stored filename and original filename are required");
        }

        String storedFilename = StringUtils.cleanPath(request.getStoredFilename());
        if (storedFilename.contains("..")) {
            throw new InvalidMediaException("Invalid stored filename");
        }

        MediaEntity existing = mediaRepository.findByStoredFilename(storedFilename).orElse(null);
        if (existing != null) {
            if (!existing.getUploaderUserId().equals(uploaderUserId)) {
                throw new UnauthorizedOperationException("You can only register your own media");
            }
            return mediaMapper.toUploadResponseDto(existing);
        }

        StatObjectResponse stat;
        try {
            stat = minioClient.statObject(StatObjectArgs.builder()
                    .bucket(bucketName)
                    .object(storedFilename)
                    .build());
        } catch (Exception ex) {
            log.error("Uploaded object not found in MinIO", ex);
            throw new InvalidMediaException("Uploaded object not found in storage");
        }

        if (request.getFileSize() != null && !request.getFileSize().equals(stat.size())) {
            throw new InvalidMediaException("Uploaded file size does not match");
        }

        String mimeType = StringUtils.hasText(request.getMimeType())
                ? request.getMimeType()
                : Optional.ofNullable(stat.contentType()).orElse("application/octet-stream");

        MediaEntity entity = MediaEntity.builder()
                .originalFilename(StringUtils.cleanPath(request.getOriginalFilename()))
                .storedFilename(storedFilename)
                .mimeType(mimeType)
                .fileSize(stat.size())
                .bucketName(bucketName)
                .uploaderUserId(uploaderUserId)
                .url(buildPublicUrl(bucketName, storedFilename))
                .build();

        MediaEntity saved = mediaRepository.save(entity);
        return mediaMapper.toUploadResponseDto(saved);
    }

    @Override
    public MediaPresignedDownloadResponseDto createPresignedDownload(Long mediaId) {
        MediaEntity entity = mediaRepository.findById(mediaId)
                .orElseThrow(() -> new MediaNotFoundException(mediaId));

        try {
            String downloadUrl = presignedMinioClient.getPresignedObjectUrl(
                    GetPresignedObjectUrlArgs.builder()
                            .method(Method.GET)
                            .bucket(entity.getBucketName())
                            .object(entity.getStoredFilename())
                            .expiry(presignedExpirySeconds)
                            .build());

            return MediaPresignedDownloadResponseDto.builder()
                    .downloadUrl(downloadUrl)
                    .expiresInSeconds((long) presignedExpirySeconds)
                    .build();
        } catch (Exception ex) {
            log.error("Failed to create presigned download URL", ex);
            throw new MediaStorageException("Failed to create presigned download URL", ex);
        }
    }

    @Override
    public void deleteMedia(Long mediaId, Long requesterUserId) {
        MediaEntity entity = mediaRepository.findById(mediaId)
                .orElseThrow(() -> new MediaNotFoundException(mediaId));

        if (!entity.getUploaderUserId().equals(requesterUserId)) {
            throw new UnauthorizedOperationException("You can only delete your own media");
        }

        try {
            minioClient.removeObject(RemoveObjectArgs.builder()
                    .bucket(entity.getBucketName())
                    .object(entity.getStoredFilename())
                    .build());
        } catch (Exception ex) {
            log.error("Failed to delete media from MinIO", ex);
            throw new MediaStorageException("Failed to delete media file", ex);
        }

        mediaRepository.delete(entity);
    }

    private String createStoredFilename(String originalFilename) {
        String extension = StringUtils.getFilenameExtension(originalFilename);
        String base = UUID.randomUUID().toString();
        if (extension == null || extension.isBlank()) {
            return base;
        }
        return base + "." + extension;
    }

    private String buildPublicUrl(String bucket, String object) {
        String base = publicUrl;
        if (base.endsWith("/")) {
            base = base.substring(0, base.length() - 1);
        }
        return base + "/" + bucket + "/" + object;
    }

}
