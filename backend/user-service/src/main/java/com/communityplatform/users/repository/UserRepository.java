package com.communityplatform.users.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.communityplatform.users.entity.UserEntity;

public interface UserRepository extends JpaRepository<UserEntity, Long> {
    Optional<UserEntity> findByUsername(String username);

    Optional<UserEntity> findByEmail(String email);

    Optional<UserEntity> findByUsernameOrEmail(String username, String email);

    Boolean existsByUsername(String username);

    Boolean existsByEmail(String email);
    
    /**
     * Find all active users (not soft-deleted) with optimized query.
     * Fetches roles with JOIN FETCH to avoid N+1 problem.
     */
    @Query("SELECT DISTINCT u FROM UserEntity u LEFT JOIN FETCH u.roles WHERE u.deletedAt IS NULL")
    List<UserEntity> findAllByDeletedAtIsNull();
    
    /**
     * Find user by username or email with roles eagerly loaded.
     * Used for authentication to avoid lazy loading issues.
     */
    @Query("SELECT u FROM UserEntity u LEFT JOIN FETCH u.roles WHERE u.username = :usernameOrEmail OR u.email = :usernameOrEmail")
    Optional<UserEntity> findByUsernameOrEmailWithRoles(@Param("usernameOrEmail") String username, @Param("usernameOrEmail") String email);
    
    /**
     * Find user by ID with roles eagerly loaded.
     * Avoids lazy loading exceptions when roles are needed.
     */
    @Query("SELECT u FROM UserEntity u LEFT JOIN FETCH u.roles WHERE u.id = :id")
    Optional<UserEntity> findByIdWithRoles(@Param("id") Long id);
}
