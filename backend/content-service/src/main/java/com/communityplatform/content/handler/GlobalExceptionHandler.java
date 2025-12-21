package com.communityplatform.content.handler;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.communityplatform.content.exception.CommentNotFoundException;
import com.communityplatform.content.exception.InvalidLikeException;
import com.communityplatform.content.exception.InvalidMediaException;
import com.communityplatform.content.exception.MediaNotFoundException;
import com.communityplatform.content.exception.MediaStorageException;
import com.communityplatform.content.exception.PostNotFoundException;
import com.communityplatform.content.exception.UnauthorizedOperationException;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;

/**
 * Global exception handler using RFC 7807 Problem Detail standard.
 * 
 * Handles all exceptions thrown by the content-service and converts them
 * into standardized ProblemDetail responses (Spring Boot 3+).
 */
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    /**
     * Handle PostNotFoundException (404 NOT FOUND).
     */
    @ExceptionHandler(PostNotFoundException.class)
    public ProblemDetail handlePostNotFound(PostNotFoundException ex, HttpServletRequest request) {
        log.error("Post not found: {}", ex.getMessage());

        ProblemDetail problem = ProblemDetail.forStatusAndDetail(
                HttpStatus.NOT_FOUND,
                ex.getMessage());
        problem.setTitle("Post Not Found");
        problem.setProperty("timestamp", LocalDateTime.now());
        problem.setProperty("path", request.getRequestURI());

        return problem;
    }

    /**
     * Handle CommentNotFoundException (404 NOT FOUND).
     */
    @ExceptionHandler(CommentNotFoundException.class)
    public ProblemDetail handleCommentNotFound(CommentNotFoundException ex, HttpServletRequest request) {
        log.error("Comment not found: {}", ex.getMessage());

        ProblemDetail problem = ProblemDetail.forStatusAndDetail(
                HttpStatus.NOT_FOUND,
                ex.getMessage());
        problem.setTitle("Comment Not Found");
        problem.setProperty("timestamp", LocalDateTime.now());
        problem.setProperty("path", request.getRequestURI());

        return problem;
    }

    /**
     * Handle MediaNotFoundException (404 NOT FOUND).
     */
    @ExceptionHandler(MediaNotFoundException.class)
    public ProblemDetail handleMediaNotFound(MediaNotFoundException ex, HttpServletRequest request) {
        log.error("Media not found: {}", ex.getMessage());

        ProblemDetail problem = ProblemDetail.forStatusAndDetail(
                HttpStatus.NOT_FOUND,
                ex.getMessage());
        problem.setTitle("Media Not Found");
        problem.setProperty("timestamp", LocalDateTime.now());
        problem.setProperty("path", request.getRequestURI());

        return problem;
    }

    /**
     * Handle UnauthorizedOperationException (403 FORBIDDEN).
     */
    @ExceptionHandler(UnauthorizedOperationException.class)
    public ProblemDetail handleUnauthorizedOperation(UnauthorizedOperationException ex, HttpServletRequest request) {
        log.error("Unauthorized operation: {}", ex.getMessage());

        ProblemDetail problem = ProblemDetail.forStatusAndDetail(
                HttpStatus.FORBIDDEN,
                ex.getMessage());
        problem.setTitle("Unauthorized Operation");
        problem.setProperty("timestamp", LocalDateTime.now());
        problem.setProperty("path", request.getRequestURI());

        return problem;
    }

    /**
     * Handle InvalidMediaException (400 BAD REQUEST).
     */
    @ExceptionHandler(InvalidMediaException.class)
    public ProblemDetail handleInvalidMedia(InvalidMediaException ex, HttpServletRequest request) {
        log.error("Invalid media: {}", ex.getMessage());

        ProblemDetail problem = ProblemDetail.forStatusAndDetail(
                HttpStatus.BAD_REQUEST,
                ex.getMessage());
        problem.setTitle("Invalid Media");
        problem.setProperty("timestamp", LocalDateTime.now());
        problem.setProperty("path", request.getRequestURI());

        return problem;
    }

    /**
     * Handle MediaStorageException (500 INTERNAL SERVER ERROR).
     */
    @ExceptionHandler(MediaStorageException.class)
    public ProblemDetail handleMediaStorage(MediaStorageException ex, HttpServletRequest request) {
        log.error("Media storage error: {}", ex.getMessage());

        ProblemDetail problem = ProblemDetail.forStatusAndDetail(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "Media storage error");
        problem.setTitle("Media Storage Error");
        problem.setProperty("timestamp", LocalDateTime.now());
        problem.setProperty("path", request.getRequestURI());

        return problem;
    }

    /**
     * Handle InvalidLikeException (400 BAD REQUEST or 409 CONFLICT).
     */
    @ExceptionHandler(InvalidLikeException.class)
    public ProblemDetail handleInvalidLike(InvalidLikeException ex, HttpServletRequest request) {
        log.error("Invalid like operation: {}", ex.getMessage());

        // Use CONFLICT for duplicate likes, BAD_REQUEST for other cases
        HttpStatus status = ex.getMessage().contains("already")
                ? HttpStatus.CONFLICT
                : HttpStatus.BAD_REQUEST;

        ProblemDetail problem = ProblemDetail.forStatusAndDetail(
                status,
                ex.getMessage());
        problem.setTitle("Invalid Like Operation");
        problem.setProperty("timestamp", LocalDateTime.now());
        problem.setProperty("path", request.getRequestURI());

        return problem;
    }

    /**
     * Handle validation errors (400 BAD REQUEST).
     * Extracts field-level validation errors from Bean Validation.
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ProblemDetail handleValidation(MethodArgumentNotValidException ex, HttpServletRequest request) {
        log.error("Validation error: {}", ex.getMessage());

        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors()
                .forEach(error -> errors.put(error.getField(), error.getDefaultMessage()));

        ProblemDetail problem = ProblemDetail.forStatusAndDetail(
                HttpStatus.BAD_REQUEST,
                "Validation failed for one or more fields");
        problem.setTitle("Invalid Request");
        problem.setProperty("errors", errors);
        problem.setProperty("timestamp", LocalDateTime.now());
        problem.setProperty("path", request.getRequestURI());

        return problem;
    }

    /**
     * Handle all other exceptions (500 INTERNAL SERVER ERROR).
     */
    @ExceptionHandler(Exception.class)
    public ProblemDetail handleGenericException(Exception ex, HttpServletRequest request) {
        log.error("Unexpected error: ", ex);

        ProblemDetail problem = ProblemDetail.forStatusAndDetail(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "An unexpected error occurred");
        problem.setTitle("Internal Server Error");
        problem.setProperty("timestamp", LocalDateTime.now());
        problem.setProperty("path", request.getRequestURI());

        return problem;
    }
}
