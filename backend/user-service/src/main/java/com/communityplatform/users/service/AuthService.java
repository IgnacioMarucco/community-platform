package com.communityplatform.users.service;

import com.communityplatform.users.dto.auth.AuthResponseDto;
import com.communityplatform.users.dto.auth.LoginRequestDto;
import com.communityplatform.users.dto.auth.RefreshTokenRequestDto;
import com.communityplatform.users.dto.auth.RegisterRequestDto;

/**
 * Service interface for authentication operations.
 */
public interface AuthService {

    /**
     * Register a new user.
     * 
     * @param registerRequest the registration details
     * @return authentication response with tokens and user info
     */
    AuthResponseDto register(RegisterRequestDto registerRequest);

    /**
     * Authenticate a user and generate tokens.
     * 
     * @param loginRequest the login credentials
     * @return authentication response with tokens and user info
     */
    AuthResponseDto login(LoginRequestDto loginRequest);

    /**
     * Refresh an access token using a refresh token.
     * 
     * @param refreshTokenRequest the refresh token
     * @return new authentication response with fresh tokens
     */
    AuthResponseDto refreshToken(RefreshTokenRequestDto refreshTokenRequest);

    /**
     * Logout a user by invalidating their refresh token.
     * 
     * @param refreshToken the refresh token to invalidate
     */
    void logout(String refreshToken);
}
