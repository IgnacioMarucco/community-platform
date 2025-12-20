package com.communityplatform.users.service.impl;

import java.time.Instant;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.communityplatform.users.dto.auth.AuthResponseDto;
import com.communityplatform.users.dto.auth.LoginRequestDto;
import com.communityplatform.users.dto.auth.RefreshTokenRequestDto;
import com.communityplatform.users.dto.auth.RegisterRequestDto;
import com.communityplatform.users.entity.RefreshTokenEntity;
import com.communityplatform.users.entity.RoleEntity;
import com.communityplatform.users.entity.UserEntity;
import com.communityplatform.users.enums.RoleName;
import com.communityplatform.users.exception.DuplicateUserException;
import com.communityplatform.users.exception.InvalidTokenException;
import com.communityplatform.users.exception.ResourceNotFoundException;
import com.communityplatform.users.exception.TokenExpiredException;
import com.communityplatform.users.repository.RefreshTokenRepository;
import com.communityplatform.users.repository.RoleRepository;
import com.communityplatform.users.repository.UserRepository;
import com.communityplatform.users.security.JwtTokenProvider;
import com.communityplatform.users.service.AuthService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Implementation of AuthService for handling authentication operations.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;
    private final com.communityplatform.users.config.JwtProperties jwtProperties;

    @Override
    @Transactional
    public AuthResponseDto register(RegisterRequestDto registerRequest) {
        log.info("Registering new user with username: {}", registerRequest.getUsername());

        // Validate username doesn't exist
        if (userRepository.existsByUsername(registerRequest.getUsername())) {
            throw new DuplicateUserException("username", registerRequest.getUsername());
        }

        // Validate email doesn't exist
        if (userRepository.existsByEmail(registerRequest.getEmail())) {
            throw new DuplicateUserException("email", registerRequest.getEmail());
        }

        // Find or create default role
        RoleEntity userRole = roleRepository.findByRoleName(RoleName.ROLE_USER)
                .orElseGet(() -> {
                    log.info("Creating default ROLE_USER");
                    RoleEntity newRole = RoleEntity.builder()
                            .roleName(RoleName.ROLE_USER)
                            .build();
                    return roleRepository.save(newRole);
                });

        // Create user entity
        Set<RoleEntity> roles = new HashSet<>();
        roles.add(userRole);

        UserEntity user = UserEntity.builder()
                .username(registerRequest.getUsername())
                .email(registerRequest.getEmail())
                .password(passwordEncoder.encode(registerRequest.getPassword()))
                .firstName(registerRequest.getFirstName())
                .lastName(registerRequest.getLastName())
                .roles(roles)
                .build();

        UserEntity savedUser;
        try {
            savedUser = userRepository.save(user);
            log.info("User registered successfully with ID: {}", savedUser.getId());
        } catch (org.springframework.dao.DataIntegrityViolationException ex) {
            // Handle race condition where user was created between check and save
            log.error("Data integrity violation during registration: {}", ex.getMessage());
            if (ex.getMessage().contains("username")) {
                throw new DuplicateUserException("username", registerRequest.getUsername());
            } else if (ex.getMessage().contains("email")) {
                throw new DuplicateUserException("email", registerRequest.getEmail());
            }
            throw ex;
        }

        // Generate tokens
        String accessToken = jwtTokenProvider.generateAccessToken(savedUser.getUsername());
        RefreshTokenEntity refreshToken = createRefreshToken(savedUser);

        return buildAuthResponse(accessToken, refreshToken.getToken(), savedUser);
    }

    @Override
    @Transactional
    public AuthResponseDto login(LoginRequestDto loginRequest) {
        log.info("Login attempt for: {}", loginRequest.getUsernameOrEmail());

        // Authenticate with Spring Security
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getUsernameOrEmail(),
                        loginRequest.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);

        // Get user from database
        UserEntity user = userRepository.findByUsernameOrEmail(
                loginRequest.getUsernameOrEmail(),
                loginRequest.getUsernameOrEmail())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "User", "username/email", loginRequest.getUsernameOrEmail()));

        // Revoke existing refresh tokens to enforce rotation per login
        refreshTokenRepository.revokeAllByUser(user);

        // Generate tokens
        String accessToken = jwtTokenProvider.generateAccessToken(user.getUsername());
        RefreshTokenEntity refreshToken = createRefreshToken(user);

        log.info("User logged in successfully: {}", user.getUsername());
        return buildAuthResponse(accessToken, refreshToken.getToken(), user);
    }

    @Override
    @Transactional
    public AuthResponseDto refreshToken(RefreshTokenRequestDto refreshTokenRequest) {
        String requestToken = refreshTokenRequest.getRefreshToken();
        log.info("Refresh token request received");

        RefreshTokenEntity refreshToken = refreshTokenRepository.findByToken(requestToken)
                .orElseThrow(() -> new InvalidTokenException("Refresh token not found"));

        // Check if revoked
        if (refreshToken.getRevoked()) {
            throw new InvalidTokenException("Refresh token has been revoked");
        }

        // Check if expired
        if (refreshToken.isExpired()) {
            throw new TokenExpiredException("Refresh token has expired");
        }

        UserEntity user = refreshToken.getUser();

        // Rotate refresh token (revoke old, issue new)
        refreshToken.setRevoked(true);
        refreshTokenRepository.save(refreshToken);
        RefreshTokenEntity newRefreshToken = createRefreshToken(user);

        // Generate new access token
        String accessToken = jwtTokenProvider.generateAccessToken(user.getUsername());
        log.info("Access token refreshed for user: {}", user.getUsername());

        return buildAuthResponse(accessToken, newRefreshToken.getToken(), user);
    }

    @Override
    @Transactional
    public void logout(String refreshToken) {
        log.info("Logout request received");

        RefreshTokenEntity token = refreshTokenRepository.findByToken(refreshToken)
                .orElseThrow(() -> new InvalidTokenException("Refresh token not found"));

        // Revoke the token
        token.setRevoked(true);
        refreshTokenRepository.save(token);

        log.info("User logged out successfully");
    }

    /**
     * Create a new refresh token for a user.
     */
    private RefreshTokenEntity createRefreshToken(UserEntity user) {
        RefreshTokenEntity refreshToken = RefreshTokenEntity.builder()
                .token(UUID.randomUUID().toString())
                .user(user)
                .expiryDate(Instant.now().plusMillis(jwtProperties.getRefreshTokenExpirationMs()))
                .revoked(false)
                .build();

        return refreshTokenRepository.save(refreshToken);
    }

    /**
     * Build the authentication response DTO.
     */
    private AuthResponseDto buildAuthResponse(String accessToken, String refreshToken, UserEntity user) {
        Set<String> roles = user.getRoles().stream()
                .map(role -> role.getRoleName().name())
                .collect(Collectors.toSet());

        return AuthResponseDto.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .tokenType("Bearer")
                .expiresIn(jwtTokenProvider.getAccessTokenExpirationSeconds())
                .userId(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .roles(roles)
                .build();
    }
}
