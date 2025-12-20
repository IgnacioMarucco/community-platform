package com.communityplatform.users.entity;

import java.time.Instant;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
 * Entity for storing refresh tokens.
 * 
 * Refresh tokens are stored in the database to allow:
 * - Token invalidation on logout
 * - Token invalidation on password change
 * - Multiple device session management
 */
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
@Data
@EqualsAndHashCode(callSuper = true)
@Table(name = "refresh_tokens")
@Entity
public class RefreshTokenEntity extends BaseEntity {

    /** The refresh token string (UUID) */
    @Column(nullable = false, unique = true, length = 255)
    private String token;

    /** The user this token belongs to */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity user;

    /** When this token expires */
    @Column(nullable = false)
    private Instant expiryDate;

    /** Whether this token has been revoked */
    @Builder.Default
    @Column(nullable = false)
    private Boolean revoked = false;

    /**
     * Check if this token is expired.
     * 
     * @return true if expired, false otherwise
     */
    public boolean isExpired() {
        return Instant.now().isAfter(this.expiryDate);
    }

    /**
     * Check if this token is valid (not expired and not revoked).
     * 
     * @return true if valid, false otherwise
     */
    public boolean isValid() {
        return !isExpired() && !revoked;
    }
}
