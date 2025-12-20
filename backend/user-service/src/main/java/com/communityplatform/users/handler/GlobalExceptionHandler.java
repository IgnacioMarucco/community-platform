package com.communityplatform.users.handler;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.communityplatform.users.exception.BadCredentialsException;
import com.communityplatform.users.exception.DuplicateUserException;
import com.communityplatform.users.exception.InvalidTokenException;
import com.communityplatform.users.exception.ResourceNotFoundException;
import com.communityplatform.users.exception.TokenExpiredException;
import com.communityplatform.users.exception.UserNotFoundException;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

/**
 * Global exception handler using RFC 7807 Problem Detail standard.
 * 
 * This class handles all exceptions thrown by the application and converts them
 * into standardized ProblemDetail responses (Spring Boot 3+).
 */
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    /**
     * Handle UserNotFoundException (404 NOT FOUND).
     */
    @ExceptionHandler(UserNotFoundException.class)
    public ProblemDetail handleUserNotFound(UserNotFoundException ex, HttpServletRequest request) {
        log.error("User not found: {}", ex.getMessage());

        ProblemDetail problem = ProblemDetail.forStatusAndDetail(
                HttpStatus.NOT_FOUND,
                ex.getMessage());
        problem.setTitle("User Not Found");
        problem.setProperty("timestamp", LocalDateTime.now());
        problem.setProperty("path", request.getRequestURI());

        return problem;
    }

    /**
     * Handle DuplicateUserException (409 CONFLICT).
     */
    @ExceptionHandler(DuplicateUserException.class)
    public ProblemDetail handleDuplicateUser(DuplicateUserException ex, HttpServletRequest request) {
        log.error("Duplicate user: {}", ex.getMessage());

        ProblemDetail problem = ProblemDetail.forStatusAndDetail(
                HttpStatus.CONFLICT,
                ex.getMessage());
        problem.setTitle("Duplicate User");
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
     * Handle InvalidTokenException (401 UNAUTHORIZED).
     */
    @ExceptionHandler(InvalidTokenException.class)
    public ProblemDetail handleInvalidToken(InvalidTokenException ex, HttpServletRequest request,
            HttpServletResponse response) {
        log.error("Invalid token: {}", ex.getMessage());

        response.setHeader(HttpHeaders.WWW_AUTHENTICATE,
                "Bearer realm=\"community-platform\", error=\"invalid_token\", error_description=\"" + ex.getMessage()
                        + "\"");

        ProblemDetail problem = ProblemDetail.forStatusAndDetail(
                HttpStatus.UNAUTHORIZED,
                ex.getMessage());
        problem.setTitle("Invalid Token");
        problem.setProperty("timestamp", LocalDateTime.now());
        problem.setProperty("path", request.getRequestURI());

        return problem;
    }

    /**
     * Handle TokenExpiredException (401 UNAUTHORIZED).
     */
    @ExceptionHandler(TokenExpiredException.class)
    public ProblemDetail handleTokenExpired(TokenExpiredException ex, HttpServletRequest request,
            HttpServletResponse response) {
        log.error("Token expired: {}", ex.getMessage());

        response.setHeader(HttpHeaders.WWW_AUTHENTICATE,
                "Bearer realm=\"community-platform\", error=\"invalid_token\", error_description=\"" + ex.getMessage()
                        + "\"");

        ProblemDetail problem = ProblemDetail.forStatusAndDetail(
                HttpStatus.UNAUTHORIZED,
                ex.getMessage());
        problem.setTitle("Token Expired");
        problem.setProperty("timestamp", LocalDateTime.now());
        problem.setProperty("path", request.getRequestURI());

        return problem;
    }

    /**
     * Handle BadCredentialsException (401 UNAUTHORIZED).
     */
    @ExceptionHandler(BadCredentialsException.class)
    public ProblemDetail handleBadCredentials(BadCredentialsException ex, HttpServletRequest request) {
        log.error("Authentication failed: {}", ex.getMessage());

        ProblemDetail problem = ProblemDetail.forStatusAndDetail(
                HttpStatus.UNAUTHORIZED,
                ex.getMessage());
        problem.setTitle("Authentication Failed");
        problem.setProperty("timestamp", LocalDateTime.now());
        problem.setProperty("path", request.getRequestURI());

        return problem;
    }

    /**
     * Handle Spring Security's BadCredentialsException (401 UNAUTHORIZED).
     */
    @ExceptionHandler(org.springframework.security.authentication.BadCredentialsException.class)
    public ProblemDetail handleSpringBadCredentials(
            org.springframework.security.authentication.BadCredentialsException ex,
            HttpServletRequest request) {
        log.error("Authentication failed: {}", ex.getMessage());

        ProblemDetail problem = ProblemDetail.forStatusAndDetail(
                HttpStatus.UNAUTHORIZED,
                "Invalid username/email or password");
        problem.setTitle("Authentication Failed");
        problem.setProperty("timestamp", LocalDateTime.now());
        problem.setProperty("path", request.getRequestURI());

        return problem;
    }

    /**
     * Handle ResourceNotFoundException (404 NOT FOUND).
     */
    @ExceptionHandler(ResourceNotFoundException.class)
    public ProblemDetail handleResourceNotFound(ResourceNotFoundException ex, HttpServletRequest request) {
        log.error("Resource not found: {}", ex.getMessage());

        ProblemDetail problem = ProblemDetail.forStatusAndDetail(
                HttpStatus.NOT_FOUND,
                ex.getMessage());
        problem.setTitle("Resource Not Found");
        problem.setProperty("timestamp", LocalDateTime.now());
        problem.setProperty("path", request.getRequestURI());

        return problem;
    }

    /**
     * Handle all other uncaught exceptions (500 INTERNAL SERVER ERROR).
     */
    @ExceptionHandler(Exception.class)
    public ProblemDetail handleGlobal(Exception ex, HttpServletRequest request) {
        log.error("Unexpected error occurred", ex);

        ProblemDetail problem = ProblemDetail.forStatusAndDetail(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "An unexpected error occurred. Please try again later.");
        problem.setTitle("Internal Server Error");
        problem.setProperty("timestamp", System.currentTimeMillis());
        problem.setProperty("path", request.getRequestURI());

        return problem;
    }
}
