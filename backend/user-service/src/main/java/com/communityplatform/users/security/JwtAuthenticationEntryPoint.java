package com.communityplatform.users.security;

import java.io.IOException;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ProblemDetail;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Entry point for handling unauthorized access attempts.
 * 
 * Returns RFC 7807 Problem Details and sets WWW-Authenticate according to RFC
 * 6750 for Bearer tokens.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {

    public static final String ATTR_ERROR = "auth_error";
    public static final String ATTR_ERROR_DESCRIPTION = "auth_error_description";
    private static final String BEARER = "Bearer";
    private static final String DEFAULT_ERROR = "invalid_token";

    private final ObjectMapper objectMapper;

    @Override
    public void commence(HttpServletRequest request,
            HttpServletResponse response,
            AuthenticationException authException) throws IOException {
        String requestUri = request.getRequestURI();
        String error = (String) request.getAttribute(ATTR_ERROR);
        String errorDescription = (String) request.getAttribute(ATTR_ERROR_DESCRIPTION);

        String resolvedError = StringUtils.hasText(error) ? error : DEFAULT_ERROR;
        String resolvedDescription = StringUtils.hasText(errorDescription)
                ? errorDescription
                : "Full authentication is required to access this resource";

        log.warn("Unauthorized access attempt to {} - {}: {}", requestUri, resolvedError, resolvedDescription);

        String challenge = String.format("%s realm=\"community-platform\", error=\"%s\", error_description=\"%s\"",
                BEARER,
                resolvedError,
                resolvedDescription.replace("\"", ""));

        response.setHeader(HttpHeaders.WWW_AUTHENTICATE, challenge);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);

        ProblemDetail problem = ProblemDetail.forStatus(HttpStatus.UNAUTHORIZED);
        problem.setTitle("Unauthorized");
        problem.setDetail(resolvedDescription);
        problem.setProperty("path", requestUri);
        problem.setProperty("timestamp", System.currentTimeMillis());

        objectMapper.writeValue(response.getOutputStream(), problem);
    }
}
