package com.communityplatform.users.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.communityplatform.users.dto.follow.FollowCountDto;
import com.communityplatform.users.dto.follow.FollowResponseDto;
import com.communityplatform.users.dto.user.UserSummaryDto;
import com.communityplatform.users.entity.UserEntity;
import com.communityplatform.users.exception.UserNotFoundException;
import com.communityplatform.users.repository.UserRepository;
import com.communityplatform.users.service.FollowService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * REST controller for managing user follow relationships.
 * All endpoints require authentication.
 */
@RestController
@RequestMapping("${api.base-path}/users")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Follow Management", description = "User follow/unfollow APIs")
public class FollowController {

    private final FollowService followService;
    private final UserRepository userRepository;

    /**
     * Follow a user.
     * POST /api/v1/users/{userId}/follow
     * 
     * The authenticated user will follow the specified user.
     */
    @Operation(summary = "Follow a user", description = "The authenticated user will start following the specified user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Successfully followed the user"),
            @ApiResponse(responseCode = "400", description = "Cannot follow yourself"),
            @ApiResponse(responseCode = "404", description = "Target user not found"),
            @ApiResponse(responseCode = "409", description = "Already following this user")
    })
    @PostMapping("/{userId}/follow")
    public ResponseEntity<Void> followUser(@PathVariable Long userId) {
        Long authenticatedUserId = getAuthenticatedUserId();
        log.info("POST /api/v1/users/{}/follow - User {} following user {}",
                userId, authenticatedUserId, userId);

        followService.followUser(authenticatedUserId, userId);
        return ResponseEntity.noContent().build();
    }

    /**
     * Unfollow a user.
     * DELETE /api/v1/users/{userId}/follow
     * 
     * The authenticated user will unfollow the specified user.
     */
    @Operation(summary = "Unfollow a user", description = "The authenticated user will stop following the specified user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Successfully unfollowed the user"),
            @ApiResponse(responseCode = "404", description = "Not following this user")
    })
    @DeleteMapping("/{userId}/follow")
    public ResponseEntity<Void> unfollowUser(@PathVariable Long userId) {
        Long authenticatedUserId = getAuthenticatedUserId();
        log.info("DELETE /api/v1/users/{}/follow - User {} unfollowing user {}",
                userId, authenticatedUserId, userId);

        followService.unfollowUser(authenticatedUserId, userId);
        return ResponseEntity.noContent().build();
    }

    /**
     * Get followers of a user.
     * GET /api/v1/users/{userId}/followers
     */
    @Operation(summary = "Get user's followers", description = "Retrieves a list of users who are following the specified user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Followers retrieved successfully")
    })
    @GetMapping("/{userId}/followers")
    public ResponseEntity<List<UserSummaryDto>> getFollowers(@PathVariable Long userId) {
        log.info("GET /api/v1/users/{}/followers - Fetching followers", userId);
        List<UserSummaryDto> followers = followService.getFollowers(userId);
        return ResponseEntity.ok(followers);
    }

    /**
     * Get users that a user is following.
     * GET /api/v1/users/{userId}/following
     */
    @Operation(summary = "Get users being followed", description = "Retrieves a list of users that the specified user is following")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Following list retrieved successfully")
    })
    @GetMapping("/{userId}/following")
    public ResponseEntity<List<UserSummaryDto>> getFollowing(@PathVariable Long userId) {
        log.info("GET /api/v1/users/{}/following - Fetching following", userId);
        List<UserSummaryDto> following = followService.getFollowing(userId);
        return ResponseEntity.ok(following);
    }

    /**
     * Check if the authenticated user is following a specific user.
     * GET /api/v1/users/{userId}/follow/check
     */
    @Operation(summary = "Check follow status", description = "Checks if the authenticated user is following the specified user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Follow status retrieved successfully")
    })
    @GetMapping("/{userId}/follow/check")
    public ResponseEntity<FollowResponseDto> isFollowing(@PathVariable Long userId) {
        Long authenticatedUserId = getAuthenticatedUserId();
        log.info("GET /api/v1/users/{}/follow/check - User {} checking follow status",
                userId, authenticatedUserId);

        FollowResponseDto response = followService.isFollowing(authenticatedUserId, userId);
        return ResponseEntity.ok(response);
    }

    /**
     * Get follower count for a user.
     * GET /api/v1/users/{userId}/followers/count
     */
    @Operation(summary = "Get follower count", description = "Retrieves the total number of followers for the specified user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Follower count retrieved successfully")
    })
    @GetMapping("/{userId}/followers/count")
    public ResponseEntity<FollowCountDto> getFollowersCount(@PathVariable Long userId) {
        log.info("GET /api/v1/users/{}/followers/count - Fetching follower count", userId);
        FollowCountDto response = followService.getFollowersCount(userId);
        return ResponseEntity.ok(response);
    }

    /**
     * Get following count for a user.
     * GET /api/v1/users/{userId}/following/count
     */
    @Operation(summary = "Get following count", description = "Retrieves the total number of users that the specified user is following")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Following count retrieved successfully")
    })
    @GetMapping("/{userId}/following/count")
    public ResponseEntity<FollowCountDto> getFollowingCount(@PathVariable Long userId) {
        log.info("GET /api/v1/users/{}/following/count - Fetching following count", userId);
        FollowCountDto response = followService.getFollowingCount(userId);
        return ResponseEntity.ok(response);
    }

    /**
     * Get the ID of the currently authenticated user.
     * 
     * @return user ID
     * @throws UserNotFoundException if user not found
     */
    private Long getAuthenticatedUserId() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        UserEntity user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException("Authenticated user not found: " + username));
        return user.getId();
    }
}
