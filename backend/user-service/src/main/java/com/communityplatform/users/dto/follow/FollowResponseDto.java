package com.communityplatform.users.dto.follow;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Response DTO for follow status check.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Follow status response")
public class FollowResponseDto {

    @Schema(description = "Whether the authenticated user is following the target user", example = "true")
    private boolean following;
}
