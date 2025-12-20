package com.communityplatform.users.service.impl;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.communityplatform.users.dto.follow.FollowCountDto;
import com.communityplatform.users.dto.follow.FollowResponseDto;
import com.communityplatform.users.dto.user.UserSummaryDto;
import com.communityplatform.users.entity.FollowEntity;
import com.communityplatform.users.entity.UserEntity;
import com.communityplatform.users.exception.AlreadyFollowingException;
import com.communityplatform.users.exception.NotFollowingException;
import com.communityplatform.users.exception.SelfFollowException;
import com.communityplatform.users.exception.UserNotFoundException;
import com.communityplatform.users.repository.FollowRepository;
import com.communityplatform.users.repository.UserRepository;
import com.communityplatform.users.service.FollowService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Implementation of FollowService for managing user follow relationships.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class FollowServiceImpl implements FollowService {

    private final FollowRepository followRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public void followUser(Long followerId, Long followingId) {
        log.debug("User {} attempting to follow user {}", followerId, followingId);

        // Validate: cannot follow yourself
        if (followerId.equals(followingId)) {
            throw new SelfFollowException();
        }

        // Validate: target user exists
        if (followingId == null || !userRepository.existsById(followingId)) {
            throw new UserNotFoundException(followingId);
        }

        // Validate: not already following
        if (followRepository.existsByFollowerIdAndFollowingId(followerId, followingId)) {
            throw new AlreadyFollowingException(followingId);
        }

        // Create follow relationship
        FollowEntity follow = FollowEntity.builder()
                .followerId(followerId)
                .followingId(followingId)
                .build();

        followRepository.save(follow);
        log.info("User {} now following user {}", followerId, followingId);
    }

    @Override
    @Transactional
    public void unfollowUser(Long followerId, Long followingId) {
        log.debug("User {} attempting to unfollow user {}", followerId, followingId);

        // Validate: must be following to unfollow
        if (!followRepository.existsByFollowerIdAndFollowingId(followerId, followingId)) {
            throw new NotFollowingException(followingId);
        }

        // Delete follow relationship
        followRepository.deleteByFollowerIdAndFollowingId(followerId, followingId);
        log.info("User {} unfollowed user {}", followerId, followingId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserSummaryDto> getFollowers(Long userId) {
        log.debug("Getting followers for user {}", userId);

        List<FollowEntity> follows = followRepository.findByFollowingId(userId);
        List<Long> followerIds = follows.stream()
                .map(FollowEntity::getFollowerId)
                .collect(Collectors.toList());

        if (followerIds.isEmpty()) {
            return java.util.Collections.emptyList();
        }

        return userRepository.findAllById(followerIds).stream()
                .map(this::toUserSummaryDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserSummaryDto> getFollowing(Long userId) {
        log.debug("Getting following for user {}", userId);

        List<FollowEntity> follows = followRepository.findByFollowerId(userId);
        List<Long> followingIds = follows.stream()
                .map(FollowEntity::getFollowingId)
                .collect(Collectors.toList());

        if (followingIds.isEmpty()) {
            return java.util.Collections.emptyList();
        }

        return userRepository.findAllById(followingIds).stream()
                .map(this::toUserSummaryDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public FollowResponseDto isFollowing(Long followerId, Long followingId) {
        log.debug("Checking if user {} is following user {}", followerId, followingId);

        boolean following = followRepository.existsByFollowerIdAndFollowingId(followerId, followingId);
        return FollowResponseDto.builder()
                .following(following)
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public FollowCountDto getFollowersCount(Long userId) {
        log.debug("Getting followers count for user {}", userId);

        long count = followRepository.countByFollowingId(userId);
        return FollowCountDto.builder()
                .count(count)
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public FollowCountDto getFollowingCount(Long userId) {
        log.debug("Getting following count for user {}", userId);

        long count = followRepository.countByFollowerId(userId);
        return FollowCountDto.builder()
                .count(count)
                .build();
    }

    /**
     * Convert UserEntity to UserSummaryDto.
     */
    private UserSummaryDto toUserSummaryDto(UserEntity user) {
        String fullName = buildFullName(user.getFirstName(), user.getLastName());

        return UserSummaryDto.builder()
                .id(user.getId())
                .username(user.getUsername())
                .fullName(fullName)
                .profilePictureUrl(user.getProfilePictureUrl())
                .build();
    }

    /**
     * Build full name from first and last name.
     */
    private String buildFullName(String firstName, String lastName) {
        if (firstName != null && lastName != null) {
            return firstName + " " + lastName;
        } else if (firstName != null) {
            return firstName;
        } else if (lastName != null) {
            return lastName;
        }
        return "";
    }
}
