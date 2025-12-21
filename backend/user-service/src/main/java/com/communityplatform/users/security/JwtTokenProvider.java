package com.communityplatform.users.security;

import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.UUID;

import javax.crypto.SecretKey;

import org.springframework.stereotype.Component;

import com.communityplatform.users.config.JwtProperties;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Component for JWT token generation and validation.
 * 
 * Handles:
 * - Access token generation with configurable expiration
 * - Token validation and parsing with issuer/audience checks
 * - Username extraction from tokens
 * 
 * Configuration is injected via JwtProperties (type-safe configuration).
 */
@Component
@Slf4j
@RequiredArgsConstructor
public class JwtTokenProvider {

    private static final String TOKEN_TYPE_CLAIM = "token_type";
    private static final String ACCESS_TOKEN_TYPE = "access";

    private final JwtProperties jwtProperties;

    /**
     * Get the signing key from the configured secret.
     * Uses HMAC-SHA256 algorithm.
     * 
     * @return SecretKey for signing/verifying tokens
     */
    private SecretKey getSigningKey() {
        byte[] keyBytes;
        try {
            keyBytes = Decoders.BASE64.decode(jwtProperties.getSecret());
        } catch (Exception e) {
            // Not base64, use raw bytes
            keyBytes = jwtProperties.getSecret().getBytes(StandardCharsets.UTF_8);
        }
        // Keys.hmacShaKeyFor will enforce minimum length (256 bits) and throw if weak
        return Keys.hmacShaKeyFor(keyBytes);
    }

    /**
     * Generate an access token for a user.
     * 
     * @param userId   stable user identifier for the subject claim
     * @param username username claim for downstream services
     * @return JWT access token string
     */
    public String generateAccessToken(Long userId, String username) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + jwtProperties.getAccessTokenExpirationMs());

        return Jwts.builder()
                .id(UUID.randomUUID().toString())
                .issuer(jwtProperties.getIssuer())
                .audience().add(jwtProperties.getAudience()).and()
                .subject(String.valueOf(userId))
                .issuedAt(now)
                .expiration(expiryDate)
                .claim("username", username)
                .claim(TOKEN_TYPE_CLAIM, ACCESS_TOKEN_TYPE)
                .signWith(getSigningKey())
                .compact();
    }

    /**
     * Extract the username from a JWT token.
     * 
     * @param token the JWT token
     * @return the username from the token's username claim
     */
    public String getUsernameFromToken(String token) {
        Claims claims = parseAndValidateClaims(token);
        return claims.get("username", String.class);
    }

    /**
     * Validate a JWT token.
     * 
     * @param token the JWT token to validate
     * @return true if valid, false otherwise
     */
    public boolean validateToken(String token) {
        parseAndValidateClaims(token);
        return true;
    }

    /**
     * Get the access token expiration time in seconds.
     * 
     * @return expiration time in seconds
     */
    public long getAccessTokenExpirationSeconds() {
        return jwtProperties.getAccessTokenExpirationMs() / 1000;
    }

    private Claims parseAndValidateClaims(String token) {
        Claims claims = Jwts.parser()
                .clockSkewSeconds(jwtProperties.getClockSkewSeconds())
                .requireIssuer(jwtProperties.getIssuer())
                .requireAudience(jwtProperties.getAudience())
                .require(TOKEN_TYPE_CLAIM, ACCESS_TOKEN_TYPE)
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();

        if (!claims.containsKey(TOKEN_TYPE_CLAIM)) {
            throw new JwtException("Token type is missing");
        }

        String subject = claims.getSubject();
        if (subject == null || subject.isBlank()) {
            throw new JwtException("Subject is required");
        }
        try {
            Long.parseLong(subject);
        } catch (NumberFormatException ex) {
            throw new JwtException("Subject must be a user id");
        }

        String username = claims.get("username", String.class);
        if (username == null || username.isBlank()) {
            throw new JwtException("Username claim is required");
        }

        return claims;
    }
}
