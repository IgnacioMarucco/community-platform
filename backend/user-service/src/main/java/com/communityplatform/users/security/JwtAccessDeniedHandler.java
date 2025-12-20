package com.communityplatform.users.security;

import java.io.IOException;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ProblemDetail;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Handles 403 Forbidden responses using RFC 7807 Problem Details and
 * adds a Bearer challenge with insufficient_scope.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class JwtAccessDeniedHandler implements AccessDeniedHandler {

    private final ObjectMapper objectMapper;

    @Override
    public void handle(HttpServletRequest request,
            HttpServletResponse response,
            AccessDeniedException accessDeniedException) throws IOException, ServletException {
        log.warn("Access denied to {}: {}", request.getRequestURI(), accessDeniedException.getMessage());

        String challenge = "Bearer realm=\"community-platform\", error=\"insufficient_scope\", "
                + "error_description=\"The token does not grant access to this resource\"";
        response.setHeader(HttpHeaders.WWW_AUTHENTICATE, challenge);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setStatus(HttpServletResponse.SC_FORBIDDEN);

        ProblemDetail problem = ProblemDetail.forStatus(HttpStatus.FORBIDDEN);
        problem.setTitle("Forbidden");
        problem.setDetail("You do not have permission to access this resource");
        problem.setProperty("path", request.getRequestURI());
        problem.setProperty("timestamp", System.currentTimeMillis());

        objectMapper.writeValue(response.getOutputStream(), problem);
    }
}
