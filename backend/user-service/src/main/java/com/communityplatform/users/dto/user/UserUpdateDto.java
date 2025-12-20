package com.communityplatform.users.dto.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for updating an existing user.
 * All fields are optional (null means "don't update this field").
 */
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class UserUpdateDto {

    @Size(min = 3, max = 50, message = "Username must be between 3 and 50 characters")
    private String username;

    @Email(message = "Email must be valid")
    @Size(max = 100, message = "Email must not exceed 100 characters")
    private String email;

    // Password updates should use dedicated endpoint for security
    // Use POST /api/v1/users/{id}/change-password instead

    @Size(max = 50, message = "First name must not exceed 50 characters")
    private String firstName;

    @Size(max = 50, message = "Last name must not exceed 50 characters")
    private String lastName;

    @Size(max = 255, message = "Profile picture URL must not exceed 255 characters")
    private String profilePictureUrl;

    @Size(max = 500, message = "Bio must not exceed 500 characters")
    private String bio;
}
