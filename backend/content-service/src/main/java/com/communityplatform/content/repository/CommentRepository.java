package com.communityplatform.content.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.communityplatform.content.entity.CommentEntity;

/**
 * Repository for Comment entity operations.
 */
@Repository
public interface CommentRepository extends JpaRepository<CommentEntity, Long> {

    /**
     * Find all active comments for a post.
     *
     * @param postId   Post ID
     * @param pageable Pagination info
     * @return Page of comments
     */
    @Query("SELECT c FROM CommentEntity c WHERE c.postId = :postId AND c.deletedAt IS NULL ORDER BY c.createdAt ASC")
    Page<CommentEntity> findByPostIdAndActive(@Param("postId") Long postId, Pageable pageable);

    /**
     * Find top-level comments (no parent) for a post.
     *
     * @param postId   Post ID
     * @param pageable Pagination info
     * @return Page of top-level comments
     */
    @Query("SELECT c FROM CommentEntity c WHERE c.postId = :postId AND c.parentCommentId IS NULL AND c.deletedAt IS NULL ORDER BY c.createdAt ASC")
    Page<CommentEntity> findTopLevelComments(@Param("postId") Long postId, Pageable pageable);

    /**
     * Find replies to a specific comment.
     *
     * @param parentCommentId Parent comment ID
     * @return List of reply comments
     */
    @Query("SELECT c FROM CommentEntity c WHERE c.parentCommentId = :parentCommentId AND c.deletedAt IS NULL ORDER BY c.createdAt ASC")
    List<CommentEntity> findRepliesByParentId(@Param("parentCommentId") Long parentCommentId);

    /**
     * Find all comments by a user.
     *
     * @param userId   User ID
     * @param pageable Pagination info
     * @return Page of comments
     */
    @Query("SELECT c FROM CommentEntity c WHERE c.userId = :userId AND c.deletedAt IS NULL ORDER BY c.createdAt DESC")
    Page<CommentEntity> findByUserIdAndActive(@Param("userId") Long userId, Pageable pageable);

    /**
     * Count active comments for a post.
     *
     * @param postId Post ID
     * @return Count of comments
     */
    @Query("SELECT COUNT(c) FROM CommentEntity c WHERE c.postId = :postId AND c.deletedAt IS NULL")
    Long countByPostId(@Param("postId") Long postId);

    /**
     * Count replies for a comment.
     *
     * @param parentCommentId Parent comment ID
     * @return Count of replies
     */
    @Query("SELECT COUNT(c) FROM CommentEntity c WHERE c.parentCommentId = :parentCommentId AND c.deletedAt IS NULL")
    Long countReplies(@Param("parentCommentId") Long parentCommentId);
}
