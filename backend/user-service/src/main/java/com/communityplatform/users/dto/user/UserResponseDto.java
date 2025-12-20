package com.communityplatform.users.dto.user;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for user responses.
 * Includes all user data plus audit fields (timestamps).
 * Note: deletedAt is intentionally excluded to not expose soft-delete status.
 */
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class UserResponseDto {

    private Long id;
    private String username;
    private String email;
    private String firstName;
    private String lastName;
    private String profilePictureUrl;
    private String bio;

    // Audit fields from BaseEntity (deletedAt excluded)
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
