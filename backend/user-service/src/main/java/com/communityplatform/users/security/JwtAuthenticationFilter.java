package com.communityplatform.users.security;

import java.io.IOException;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import com.communityplatform.users.security.JwtAuthenticationEntryPoint;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * JWT authentication filter that intercepts all requests.
 * 
 * Extracts the JWT token from the Authorization header,
 * validates it, and sets the authentication in the SecurityContext.
 * 
 * NOTE: This is NOT annotated with @Component to prevent automatic
 * registration as a servlet filter. It is manually registered in
 * SecurityConfig.
 */
@RequiredArgsConstructor
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final String AUTHORIZATION_HEADER = "Authorization";
    private static final String BEARER_PREFIX = "Bearer ";

    private final JwtTokenProvider jwtTokenProvider;
    private final UserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {
        try {
            String jwt = extractJwtFromRequest(request);

            if (StringUtils.hasText(jwt) && jwtTokenProvider.validateToken(jwt)) {
                String username = jwtTokenProvider.getUsernameFromToken(jwt);
                UserDetails userDetails = userDetailsService.loadUserByUsername(username);

                if (userDetails.isAccountNonLocked() && userDetails.isEnabled()
                        && userDetails.isAccountNonExpired() && userDetails.isCredentialsNonExpired()) {
                    UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                            userDetails,
                            null,
                            userDetails.getAuthorities());

                    authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                } else {
                    markAuthError(request, "invalid_token", "User account is disabled or locked");
                }
            }
        } catch (ExpiredJwtException ex) {
            markAuthError(request, "invalid_token", "Token expired");
            log.warn("Expired JWT: {}", ex.getMessage());
        } catch (JwtException | IllegalArgumentException ex) {
            markAuthError(request, "invalid_token", "Invalid token");
            log.warn("Invalid JWT: {}", ex.getMessage());
        } catch (Exception ex) {
            markAuthError(request, "invalid_token", "Authentication failed");
            log.error("Could not set user authentication in security context", ex);
        }

        filterChain.doFilter(request, response);
    }

    /**
     * Extract JWT token from the Authorization header.
     * 
     * @param request the HTTP request
     * @return the JWT token or null if not present
     */
    private String extractJwtFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader(AUTHORIZATION_HEADER);
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith(BEARER_PREFIX)) {
            return bearerToken.substring(BEARER_PREFIX.length());
        }
        return null;
    }

    private void markAuthError(HttpServletRequest request, String error, String description) {
        request.setAttribute(JwtAuthenticationEntryPoint.ATTR_ERROR, error);
        request.setAttribute(JwtAuthenticationEntryPoint.ATTR_ERROR_DESCRIPTION, description);
        SecurityContextHolder.clearContext();
    }
}
