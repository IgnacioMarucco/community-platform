package com.communityplatform.content.service.impl;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.communityplatform.content.dto.like.LikeResponseDto;
import com.communityplatform.content.entity.LikeEntity;
import com.communityplatform.content.exception.CommentNotFoundException;
import com.communityplatform.content.exception.InvalidLikeException;
import com.communityplatform.content.exception.PostNotFoundException;
import com.communityplatform.content.mapper.LikeMapper;
import com.communityplatform.content.repository.CommentRepository;
import com.communityplatform.content.repository.LikeRepository;
import com.communityplatform.content.repository.PostRepository;
import com.communityplatform.content.service.CommentService;
import com.communityplatform.content.service.LikeService;
import com.communityplatform.content.service.PostService;
import com.communityplatform.content.UserServiceClient;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Implementation of LikeService.
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class LikeServiceImpl implements LikeService {

    private final LikeRepository likeRepository;
    private final PostRepository postRepository;
    private final CommentRepository commentRepository;
    private final LikeMapper likeMapper;
    private final PostService postService;
    private final CommentService commentService;
    private final UserServiceClient userServiceClient;

    @Override
    public LikeResponseDto likePost(Long postId, Long userId) {
        log.debug("User {} liking post {}", userId, postId);

        // Verify post exists
        postRepository.findByIdAndActive(postId)
                .orElseThrow(() -> new PostNotFoundException(postId));

        // Check if already liked
        if (likeRepository.existsByUserIdAndPostId(userId, postId)) {
            throw InvalidLikeException.alreadyLiked();
        }

        LikeEntity like = LikeEntity.builder()
                .userId(userId)
                .postId(postId)
                .build();

        LikeEntity saved = likeRepository.save(like);

        // Increment post like count
        postService.incrementLikeCount(postId);

        log.info("User {} liked post {}", userId, postId);
        return enrichLike(likeMapper.toResponseDto(saved));
    }

    @Override
    public void unlikePost(Long postId, Long userId) {
        log.debug("User {} unliking post {}", userId, postId);

        LikeEntity like = likeRepository.findByUserIdAndPostId(userId, postId)
                .orElseThrow(InvalidLikeException::notLiked);

        likeRepository.delete(like);

        // Decrement post like count
        postService.decrementLikeCount(postId);

        log.info("User {} unliked post {}", userId, postId);
    }

    @Override
    public LikeResponseDto likeComment(Long commentId, Long userId) {
        log.debug("User {} liking comment {}", userId, commentId);

        // Verify comment exists
        commentRepository.findById(commentId)
                .orElseThrow(() -> new CommentNotFoundException(commentId));

        // Check if already liked
        if (likeRepository.existsByUserIdAndCommentId(userId, commentId)) {
            throw InvalidLikeException.alreadyLiked();
        }

        LikeEntity like = LikeEntity.builder()
                .userId(userId)
                .commentId(commentId)
                .build();

        LikeEntity saved = likeRepository.save(like);

        // Increment comment like count
        commentService.incrementLikeCount(commentId);

        log.info("User {} liked comment {}", userId, commentId);
        return enrichLike(likeMapper.toResponseDto(saved));
    }

    @Override
    public void unlikeComment(Long commentId, Long userId) {
        log.debug("User {} unliking comment {}", userId, commentId);

        LikeEntity like = likeRepository.findByUserIdAndCommentId(userId, commentId)
                .orElseThrow(InvalidLikeException::notLiked);

        likeRepository.delete(like);

        // Decrement comment like count
        commentService.decrementLikeCount(commentId);

        log.info("User {} unliked comment {}", userId, commentId);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean hasLikedPost(Long postId, Long userId) {
        return likeRepository.existsByUserIdAndPostId(userId, postId);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean hasLikedComment(Long commentId, Long userId) {
        return likeRepository.existsByUserIdAndCommentId(userId, commentId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<LikeResponseDto> getPostLikes(Long postId) {
        log.debug("Getting likes for post: {}", postId);
        return likeRepository.findByPostId(postId)
                .stream()
                .map(likeMapper::toResponseDto)
                .map(this::enrichLike)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<LikeResponseDto> getCommentLikes(Long commentId) {
        log.debug("Getting likes for comment: {}", commentId);
        return likeRepository.findByCommentId(commentId)
                .stream()
                .map(likeMapper::toResponseDto)
                .map(this::enrichLike)
                .collect(Collectors.toList());
    }

    private LikeResponseDto enrichLike(LikeResponseDto dto) {
        if (dto == null || dto.getUserId() == null) {
            return dto;
        }
        userServiceClient.getUserById(dto.getUserId())
                .ifPresent(profile -> dto.setUsername(profile.getUsername()));
        return dto;
    }
}
