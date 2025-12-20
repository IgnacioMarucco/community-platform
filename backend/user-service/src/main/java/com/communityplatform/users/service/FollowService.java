package com.communityplatform.users.service;

import java.util.List;

import com.communityplatform.users.dto.follow.FollowCountDto;
import com.communityplatform.users.dto.follow.FollowResponseDto;
import com.communityplatform.users.dto.user.UserSummaryDto;

/**
 * Service interface for managing user follow relationships.
 */
public interface FollowService {

    /**
     * Follow a user.
     * 
     * @param followerId  ID of the user doing the following
     * @param followingId ID of the user to be followed
     */
    void followUser(Long followerId, Long followingId);

    /**
     * Unfollow a user.
     * 
     * @param followerId  ID of the user doing the unfollowing
     * @param followingId ID of the user to be unfollowed
     */
    void unfollowUser(Long followerId, Long followingId);

    /**
     * Get all followers of a user.
     * 
     * @param userId ID of the user
     * @return list of user summaries
     */
    List<UserSummaryDto> getFollowers(Long userId);

    /**
     * Get all users that a user is following.
     * 
     * @param userId ID of the user
     * @return list of user summaries
     */
    List<UserSummaryDto> getFollowing(Long userId);

    /**
     * Check if one user is following another.
     * 
     * @param followerId  ID of the potential follower
     * @param followingId ID of the user being checked
     * @return follow status
     */
    FollowResponseDto isFollowing(Long followerId, Long followingId);

    /**
     * Get count of followers for a user.
     * 
     * @param userId ID of the user
     * @return follower count
     */
    FollowCountDto getFollowersCount(Long userId);

    /**
     * Get count of users that a user is following.
     * 
     * @param userId ID of the user
     * @return following count
     */
    FollowCountDto getFollowingCount(Long userId);
}
