package com.communityplatform.users.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.communityplatform.users.entity.RefreshTokenEntity;
import com.communityplatform.users.entity.UserEntity;

/**
 * Repository for RefreshTokenEntity operations.
 */
public interface RefreshTokenRepository extends JpaRepository<RefreshTokenEntity, Long> {

    /**
     * Find a refresh token by its token string.
     * 
     * @param token the token string
     * @return Optional containing the refresh token if found
     */
    Optional<RefreshTokenEntity> findByToken(String token);

    /**
     * Delete all refresh tokens for a specific user.
     * Used when user logs out from all devices or changes password.
     * 
     * @param user the user entity
     */
    @Modifying
    @Query("DELETE FROM RefreshTokenEntity rt WHERE rt.user = :user")
    void deleteByUser(@Param("user") UserEntity user);

    /**
     * Revoke all tokens for a specific user.
     * 
     * @param user the user entity
     */
    @Modifying
    @Query("UPDATE RefreshTokenEntity rt SET rt.revoked = true WHERE rt.user = :user")
    void revokeAllByUser(@Param("user") UserEntity user);
}
