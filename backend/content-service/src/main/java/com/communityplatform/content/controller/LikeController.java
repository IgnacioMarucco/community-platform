package com.communityplatform.content.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.communityplatform.content.dto.like.LikeResponseDto;
import com.communityplatform.content.service.LikeService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

/**
 * REST Controller for Like management.
 */
@RestController
@RequestMapping("${api.base-path}")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Likes", description = "Like management endpoints")
public class LikeController {

        private final LikeService likeService;

        // ===== POST LIKES =====

        @Operation(summary = "Like a post")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "201", description = "Post liked successfully"),
                        @ApiResponse(responseCode = "409", description = "Already liked"),
                        @ApiResponse(responseCode = "404", description = "Post not found")
        })
        @PostMapping("/posts/{postId}/like")
        public ResponseEntity<LikeResponseDto> likePost(
                        @PathVariable Long postId,
                        @RequestHeader("X-User-Id") Long userId) {
                log.info("User {} liking post {}", userId, postId);
                LikeResponseDto response = likeService.likePost(postId, userId);
                return ResponseEntity.status(HttpStatus.CREATED).body(response);
        }

        @Operation(summary = "Unlike a post")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "204", description = "Post unliked successfully"),
                        @ApiResponse(responseCode = "400", description = "Not liked"),
                        @ApiResponse(responseCode = "404", description = "Post not found")
        })
        @DeleteMapping("/posts/{postId}/like")
        public ResponseEntity<Void> unlikePost(
                        @PathVariable Long postId,
                        @RequestHeader("X-User-Id") Long userId) {
                log.info("User {} unliking post {}", userId, postId);
                likeService.unlikePost(postId, userId);
                return ResponseEntity.noContent().build();
        }

        @Operation(summary = "Unlike a post (alias)")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "204", description = "Post unliked successfully"),
                        @ApiResponse(responseCode = "400", description = "Not liked"),
                        @ApiResponse(responseCode = "404", description = "Post not found")
        })
        @PostMapping("/posts/{postId}/unlike")
        public ResponseEntity<Void> unlikePostAlias(
                        @PathVariable Long postId,
                        @RequestHeader("X-User-Id") Long userId) {
                log.info("User {} unliking post {} via alias", userId, postId);
                likeService.unlikePost(postId, userId);
                return ResponseEntity.noContent().build();
        }

        @Operation(summary = "Check if user liked a post")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Like status retrieved")
        })
        @GetMapping("/posts/{postId}/like/check")
        public ResponseEntity<Boolean> hasLikedPost(
                        @PathVariable Long postId,
                        @RequestHeader("X-User-Id") Long userId) {
                log.info("Checking if user {} liked post {}", userId, postId);
                boolean hasLiked = likeService.hasLikedPost(postId, userId);
                return ResponseEntity.ok(hasLiked);
        }

        @Operation(summary = "Get all likes for a post")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Likes retrieved successfully")
        })
        @GetMapping("/posts/{postId}/likes")
        public ResponseEntity<List<LikeResponseDto>> getPostLikes(@PathVariable Long postId) {
                log.info("Getting likes for post {}", postId);
                List<LikeResponseDto> response = likeService.getPostLikes(postId);
                return ResponseEntity.ok(response);
        }

        // ===== COMMENT LIKES =====

        @Operation(summary = "Like a comment")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "201", description = "Comment liked successfully"),
                        @ApiResponse(responseCode = "409", description = "Already liked"),
                        @ApiResponse(responseCode = "404", description = "Comment not found")
        })
        @PostMapping("/comments/{commentId}/like")
        public ResponseEntity<LikeResponseDto> likeComment(
                        @PathVariable Long commentId,
                        @RequestHeader("X-User-Id") Long userId) {
                log.info("User {} liking comment {}", userId, commentId);
                LikeResponseDto response = likeService.likeComment(commentId, userId);
                return ResponseEntity.status(HttpStatus.CREATED).body(response);
        }

        @Operation(summary = "Unlike a comment")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "204", description = "Comment unliked successfully"),
                        @ApiResponse(responseCode = "400", description = "Not liked"),
                        @ApiResponse(responseCode = "404", description = "Comment not found")
        })
        @DeleteMapping("/comments/{commentId}/like")
        public ResponseEntity<Void> unlikeComment(
                        @PathVariable Long commentId,
                        @RequestHeader("X-User-Id") Long userId) {
                log.info("User {} unliking comment {}", userId, commentId);
                likeService.unlikeComment(commentId, userId);
                return ResponseEntity.noContent().build();
        }

        @Operation(summary = "Check if user liked a comment")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Like status retrieved")
        })
        @GetMapping("/comments/{commentId}/like/check")
        public ResponseEntity<Boolean> hasLikedComment(
                        @PathVariable Long commentId,
                        @RequestHeader("X-User-Id") Long userId) {
                log.info("Checking if user {} liked comment {}", userId, commentId);
                boolean hasLiked = likeService.hasLikedComment(commentId, userId);
                return ResponseEntity.ok(hasLiked);
        }

        @Operation(summary = "Get all likes for a comment")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Likes retrieved successfully")
        })
        @GetMapping("/comments/{commentId}/likes")
        public ResponseEntity<List<LikeResponseDto>> getCommentLikes(@PathVariable Long commentId) {
                log.info("Getting likes for comment {}", commentId);
                List<LikeResponseDto> response = likeService.getCommentLikes(commentId);
                return ResponseEntity.ok(response);
        }
}
