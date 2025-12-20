package com.communityplatform.users.dto.auth;

import java.util.Set;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for authentication responses (login/register).
 * Contains tokens and user information.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuthResponseDto {

    /** JWT access token */
    private String accessToken;

    /** Refresh token for obtaining new access tokens */
    private String refreshToken;

    /** Token type (always "Bearer") */
    @Builder.Default
    private String tokenType = "Bearer";

    /** Access token expiration time in seconds */
    private Long expiresIn;

    /** User's ID */
    private Long userId;

    /** User's username */
    private String username;

    /** User's email */
    private String email;

    /** User's roles */
    private Set<String> roles;
}
