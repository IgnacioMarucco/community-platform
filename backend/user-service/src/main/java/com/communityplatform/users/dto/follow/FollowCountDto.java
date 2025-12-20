package com.communityplatform.users.dto.follow;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Response DTO for follower/following count.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Follow count response")
public class FollowCountDto {

    @Schema(description = "Total count of followers or following", example = "42")
    private long count;
}
