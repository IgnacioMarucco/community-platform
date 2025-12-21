package com.communityplatform.content.service;

import com.communityplatform.content.dto.like.LikeResponseDto;

import java.util.List;

/**
 * Service interface for Like operations.
 */
public interface LikeService {

    /**
     * Like a post.
     */
    LikeResponseDto likePost(Long postId, Long userId);

    /**
     * Unlike a post.
     */
    void unlikePost(Long postId, Long userId);

    /**
     * Like a comment.
     */
    LikeResponseDto likeComment(Long commentId, Long userId);

    /**
     * Unlike a comment.
     */
    void unlikeComment(Long commentId, Long userId);

    /**
     * Check if user has liked a post.
     */
    boolean hasLikedPost(Long postId, Long userId);

    /**
     * Check if user has liked a comment.
     */
    boolean hasLikedComment(Long commentId, Long userId);

    /**
     * Get all likes for a post.
     */
    List<LikeResponseDto> getPostLikes(Long postId);

    /**
     * Get all likes for a comment.
     */
    List<LikeResponseDto> getCommentLikes(Long commentId);
}
