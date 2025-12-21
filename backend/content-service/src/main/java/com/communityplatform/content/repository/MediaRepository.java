package com.communityplatform.content.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.communityplatform.content.entity.MediaEntity;

/**
 * Repository for Media entity operations.
 */
@Repository
public interface MediaRepository extends JpaRepository<MediaEntity, Long> {

    /**
     * Find all media uploaded by a user.
     *
     * @param uploaderUserId Uploader user ID
     * @param pageable       Pagination info
     * @return Page of media
     */
    @Query("SELECT m FROM MediaEntity m WHERE m.uploaderUserId = :uploaderUserId ORDER BY m.createdAt DESC")
    Page<MediaEntity> findByUploaderUserId(@Param("uploaderUserId") Long uploaderUserId, Pageable pageable);

    /**
     * Find media by stored filename.
     *
     * @param storedFilename Stored filename
     * @return Optional media
     */
    @Query("SELECT m FROM MediaEntity m WHERE m.storedFilename = :storedFilename")
    java.util.Optional<MediaEntity> findByStoredFilename(@Param("storedFilename") String storedFilename);

    /**
     * Find media by MIME type (e.g., all images, all videos).
     *
     * @param mimeTypePrefix MIME type prefix (e.g., "image/", "video/")
     * @param pageable       Pagination info
     * @return Page of media
     */
    @Query("SELECT m FROM MediaEntity m WHERE m.mimeType LIKE CONCAT(:mimeTypePrefix, '%') ORDER BY m.createdAt DESC")
    Page<MediaEntity> findByMimeTypeStartsWith(@Param("mimeTypePrefix") String mimeTypePrefix, Pageable pageable);

    /**
     * Count media files by uploader.
     *
     * @param uploaderUserId Uploader user ID
     * @return Count of media files
     */
    @Query("SELECT COUNT(m) FROM MediaEntity m WHERE m.uploaderUserId = :uploaderUserId")
    Long countByUploaderUserId(@Param("uploaderUserId") Long uploaderUserId);

    /**
     * Calculate total storage used by user (in bytes).
     *
     * @param uploaderUserId Uploader user ID
     * @return Total file size in bytes
     */
    @Query("SELECT COALESCE(SUM(m.fileSize), 0) FROM MediaEntity m WHERE m.uploaderUserId = :uploaderUserId")
    Long calculateTotalStorageByUser(@Param("uploaderUserId") Long uploaderUserId);
}
