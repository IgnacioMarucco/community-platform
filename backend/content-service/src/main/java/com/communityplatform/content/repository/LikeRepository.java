package com.communityplatform.content.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.communityplatform.content.entity.LikeEntity;

/**
 * Repository for Like entity operations.
 */
@Repository
public interface LikeRepository extends JpaRepository<LikeEntity, Long> {

    /**
     * Check if user has liked a post.
     *
     * @param userId User ID
     * @param postId Post ID
     * @return true if like exists
     */
    @Query("SELECT CASE WHEN COUNT(l) > 0 THEN true ELSE false END FROM LikeEntity l WHERE l.userId = :userId AND l.postId = :postId")
    boolean existsByUserIdAndPostId(@Param("userId") Long userId, @Param("postId") Long postId);

    /**
     * Check if user has liked a comment.
     *
     * @param userId    User ID
     * @param commentId Comment ID
     * @return true if like exists
     */
    @Query("SELECT CASE WHEN COUNT(l) > 0 THEN true ELSE false END FROM LikeEntity l WHERE l.userId = :userId AND l.commentId = :commentId")
    boolean existsByUserIdAndCommentId(@Param("userId") Long userId, @Param("commentId") Long commentId);

    /**
     * Find like by user and post.
     *
     * @param userId User ID
     * @param postId Post ID
     * @return Optional like
     */
    Optional<LikeEntity> findByUserIdAndPostId(Long userId, Long postId);

    /**
     * Find like by user and comment.
     *
     * @param userId    User ID
     * @param commentId Comment ID
     * @return Optional like
     */
    Optional<LikeEntity> findByUserIdAndCommentId(Long userId, Long commentId);

    /**
     * Find all likes for a post.
     *
     * @param postId Post ID
     * @return List of likes
     */
    @Query("SELECT l FROM LikeEntity l WHERE l.postId = :postId ORDER BY l.createdAt DESC")
    List<LikeEntity> findByPostId(@Param("postId") Long postId);

    /**
     * Find all likes for a comment.
     *
     * @param commentId Comment ID
     * @return List of likes
     */
    @Query("SELECT l FROM LikeEntity l WHERE l.commentId = :commentId ORDER BY l.createdAt DESC")
    List<LikeEntity> findByCommentId(@Param("commentId") Long commentId);

    /**
     * Count likes for a post.
     *
     * @param postId Post ID
     * @return Count of likes
     */
    @Query("SELECT COUNT(l) FROM LikeEntity l WHERE l.postId = :postId")
    Long countByPostId(@Param("postId") Long postId);

    /**
     * Count likes for a comment.
     *
     * @param commentId Comment ID
     * @return Count of likes
     */
    @Query("SELECT COUNT(l) FROM LikeEntity l WHERE l.commentId = :commentId")
    Long countByCommentId(@Param("commentId") Long commentId);

    /**
     * Delete like by user and post.
     *
     * @param userId User ID
     * @param postId Post ID
     */
    void deleteByUserIdAndPostId(Long userId, Long postId);

    /**
     * Delete like by user and comment.
     *
     * @param userId    User ID
     * @param commentId Comment ID
     */
    void deleteByUserIdAndCommentId(Long userId, Long commentId);
}
