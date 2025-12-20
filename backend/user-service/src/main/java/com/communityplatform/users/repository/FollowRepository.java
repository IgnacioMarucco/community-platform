package com.communityplatform.users.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.communityplatform.users.entity.FollowEntity;

/**
 * Repository for managing follow relationships between users.
 */
@Repository
public interface FollowRepository extends JpaRepository<FollowEntity, Long> {

    /**
     * Find a specific follow relationship.
     */
    Optional<FollowEntity> findByFollowerIdAndFollowingId(Long followerId, Long followingId);

    /**
     * Check if a follow relationship exists.
     */
    boolean existsByFollowerIdAndFollowingId(Long followerId, Long followingId);

    /**
     * Delete a specific follow relationship.
     */
    @Transactional
    @Modifying
    void deleteByFollowerIdAndFollowingId(Long followerId, Long followingId);

    /**
     * Find all users that a specific user is following.
     */
    List<FollowEntity> findByFollowerId(Long followerId);

    /**
     * Find all followers of a specific user.
     */
    List<FollowEntity> findByFollowingId(Long followingId);

    /**
     * Count followers of a specific user.
     */
    long countByFollowingId(Long userId);

    /**
     * Count users that a specific user is following.
     */
    long countByFollowerId(Long userId);
}
