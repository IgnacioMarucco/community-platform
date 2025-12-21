package com.communityplatform.users.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.communityplatform.users.dto.user.UserSummaryDto;
import com.communityplatform.users.exception.AlreadyFollowingException;
import com.communityplatform.users.exception.BadCredentialsException;
import com.communityplatform.users.service.FollowService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * REST controller for /follows endpoints used by the gateway tests.
 */
@RestController
@RequestMapping("${api.base-path}/follows")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Follows", description = "Follow endpoints compatible with the gateway tests")
public class FollowsController {

    private final FollowService followService;

    @Operation(summary = "Follow a user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Successfully followed (or already following)"),
            @ApiResponse(responseCode = "400", description = "Cannot follow yourself"),
            @ApiResponse(responseCode = "404", description = "Target user not found")
    })
    @PostMapping("/{userId}")
    public ResponseEntity<Void> followUser(@PathVariable Long userId,
            @RequestHeader("X-User-Id") Long authenticatedUserId) {
        Long resolvedUserId = requireAuthenticatedUserId(authenticatedUserId);
        log.info("POST /api/v1/follows/{} - User {} following user {}", userId, resolvedUserId, userId);

        try {
            followService.followUser(resolvedUserId, userId);
        } catch (AlreadyFollowingException ex) {
            log.info("User {} already following user {}", resolvedUserId, userId);
        }

        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Unfollow a user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Successfully unfollowed"),
            @ApiResponse(responseCode = "404", description = "Not following this user")
    })
    @DeleteMapping("/{userId}")
    public ResponseEntity<Void> unfollowUser(@PathVariable Long userId,
            @RequestHeader("X-User-Id") Long authenticatedUserId) {
        Long resolvedUserId = requireAuthenticatedUserId(authenticatedUserId);
        log.info("DELETE /api/v1/follows/{} - User {} unfollowing user {}", userId, resolvedUserId, userId);

        followService.unfollowUser(resolvedUserId, userId);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Get a user's followers")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Followers retrieved successfully")
    })
    @GetMapping("/{userId}/followers")
    public ResponseEntity<List<UserSummaryDto>> getFollowers(@PathVariable Long userId) {
        log.info("GET /api/v1/follows/{}/followers - Fetching followers", userId);
        List<UserSummaryDto> followers = followService.getFollowers(userId);
        return ResponseEntity.ok(followers);
    }

    @Operation(summary = "Get current user's following list")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Following list retrieved successfully")
    })
    @GetMapping("/me/following")
    public ResponseEntity<List<UserSummaryDto>> getCurrentUserFollowing(
            @RequestHeader("X-User-Id") Long authenticatedUserId) {
        Long resolvedUserId = requireAuthenticatedUserId(authenticatedUserId);
        log.info("GET /api/v1/follows/me/following - Fetching following for user {}", resolvedUserId);
        List<UserSummaryDto> following = followService.getFollowing(resolvedUserId);
        return ResponseEntity.ok(following);
    }

    @Operation(summary = "Check if current user follows another user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Follow status retrieved successfully")
    })
    @GetMapping("/{userId}/is-following")
    public ResponseEntity<Boolean> isFollowing(@PathVariable Long userId,
            @RequestHeader("X-User-Id") Long authenticatedUserId) {
        Long resolvedUserId = requireAuthenticatedUserId(authenticatedUserId);
        log.info("GET /api/v1/follows/{}/is-following - User {} checking follow status", userId,
                resolvedUserId);

        boolean following = followService.isFollowing(resolvedUserId, userId).isFollowing();
        return ResponseEntity.ok(following);
    }

    private Long requireAuthenticatedUserId(Long authenticatedUserId) {
        if (authenticatedUserId == null) {
            throw new BadCredentialsException("Missing authenticated user id");
        }
        return authenticatedUserId;
    }
}
