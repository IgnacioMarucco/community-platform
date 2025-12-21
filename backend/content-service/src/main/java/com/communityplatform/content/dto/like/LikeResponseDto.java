package com.communityplatform.content.dto.like;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for like response.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LikeResponseDto {

    /**
     * Like ID.
     */
    private Long id;

    /**
     * User ID who gave the like.
     */
    private Long userId;

    /**
     * Username of the user who liked.
     */
    private String username;

    /**
     * Post ID (if like is on a post).
     */
    private Long postId;

    /**
     * Comment ID (if like is on a comment).
     */
    private Long commentId;

    /**
     * Timestamp when like was created.
     */
    private LocalDateTime createdAt;
}
