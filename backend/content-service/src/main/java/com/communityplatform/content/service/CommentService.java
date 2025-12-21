package com.communityplatform.content.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.communityplatform.content.dto.comment.CommentCreateDto;
import com.communityplatform.content.dto.comment.CommentResponseDto;
import com.communityplatform.content.dto.comment.CommentUpdateDto;

import java.util.List;

/**
 * Service interface for Comment operations.
 */
public interface CommentService {

    /**
     * Create a new comment.
     */
    CommentResponseDto createComment(CommentCreateDto dto);

    /**
     * Get comment by ID.
     */
    CommentResponseDto getCommentById(Long commentId);

    /**
     * Update an existing comment.
     */
    CommentResponseDto updateComment(Long commentId, CommentUpdateDto dto, Long currentUserId);

    /**
     * Delete comment (soft delete).
     */
    void deleteComment(Long commentId, Long currentUserId);

    /**
     * Get all comments for a post.
     */
    Page<CommentResponseDto> getCommentsByPostId(Long postId, Pageable pageable);

    /**
     * Get top-level comments for a post (no parent).
     */
    Page<CommentResponseDto> getTopLevelComments(Long postId, Pageable pageable);

    /**
     * Get replies for a comment.
     */
    List<CommentResponseDto> getReplies(Long parentCommentId);

    /**
     * Increment like count.
     */
    void incrementLikeCount(Long commentId);

    /**
     * Decrement like count.
     */
    void decrementLikeCount(Long commentId);
}
