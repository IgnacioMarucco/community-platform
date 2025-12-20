package com.communityplatform.users.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.communityplatform.users.dto.auth.AuthResponseDto;
import com.communityplatform.users.dto.auth.LoginRequestDto;
import com.communityplatform.users.dto.auth.RefreshTokenRequestDto;
import com.communityplatform.users.dto.auth.RegisterRequestDto;
import com.communityplatform.users.service.AuthService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * REST controller for authentication operations.
 * All endpoints are public (no authentication required).
 */
@RestController
@RequestMapping("${api.base-path}/auth")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Authentication", description = "Authentication and authorization APIs")
public class AuthController {

    private final AuthService authService;

    /**
     * Register a new user.
     * POST /api/v1/auth/register
     */
    @Operation(summary = "Register a new user", description = "Creates a new user account and returns authentication tokens")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "User registered successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input data"),
            @ApiResponse(responseCode = "409", description = "Username or email already exists")
    })
    @PostMapping("/register")
    public ResponseEntity<AuthResponseDto> register(@Valid @RequestBody RegisterRequestDto registerRequest) {
        log.info("POST /api/v1/auth/register - Registering user: {}", registerRequest.getUsername());
        AuthResponseDto response = authService.register(registerRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Authenticate a user.
     * POST /api/v1/auth/login
     */
    @Operation(summary = "Login", description = "Authenticates a user and returns access and refresh tokens")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Login successful"),
            @ApiResponse(responseCode = "401", description = "Invalid credentials")
    })
    @PostMapping("/login")
    public ResponseEntity<AuthResponseDto> login(@Valid @RequestBody LoginRequestDto loginRequest) {
        log.info("POST /api/v1/auth/login - Login attempt for: {}", loginRequest.getUsernameOrEmail());
        AuthResponseDto response = authService.login(loginRequest);
        return ResponseEntity.ok(response);
    }

    /**
     * Refresh access token.
     * POST /api/v1/auth/refresh
     */
    @Operation(summary = "Refresh token", description = "Generates a new access token using a valid refresh token")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Token refreshed successfully"),
            @ApiResponse(responseCode = "401", description = "Invalid or expired refresh token")
    })
    @PostMapping("/refresh")
    public ResponseEntity<AuthResponseDto> refreshToken(
            @Valid @RequestBody RefreshTokenRequestDto refreshTokenRequest) {
        log.info("POST /api/v1/auth/refresh - Refresh token request");
        AuthResponseDto response = authService.refreshToken(refreshTokenRequest);
        return ResponseEntity.ok(response);
    }

    /**
     * Logout user (invalidate refresh token).
     * POST /api/v1/auth/logout
     */
    @Operation(summary = "Logout", description = "Invalidates the refresh token, effectively logging out the user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Logout successful"),
            @ApiResponse(responseCode = "401", description = "Invalid refresh token")
    })
    @PostMapping("/logout")
    public ResponseEntity<Void> logout(@Valid @RequestBody RefreshTokenRequestDto logoutRequest) {
        log.info("POST /api/v1/auth/logout - Logout request");
        authService.logout(logoutRequest.getRefreshToken());
        return ResponseEntity.noContent().build();
    }
}
