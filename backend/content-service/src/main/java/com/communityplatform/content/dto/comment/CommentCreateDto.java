package com.communityplatform.content.dto.comment;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for creating a new comment.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CommentCreateDto {

    /**
     * Post ID this comment belongs to.
     */
    private Long postId;

    /**
     * User ID of the comment author.
     */
    private Long userId;

    /**
     * Comment content.
     */
    @NotBlank(message = "Content cannot be blank")
    @Size(min = 1, max = 1000, message = "Content must be between 1 and 1000 characters")
    private String content;

    /**
     * Optional parent comment ID for nested comments/replies.
     */
    private Long parentCommentId;
}
