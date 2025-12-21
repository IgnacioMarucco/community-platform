package com.communityplatform.users.config;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.communityplatform.users.security.CustomUserDetailsService;
import com.communityplatform.users.security.JwtAccessDeniedHandler;
import com.communityplatform.users.security.JwtAuthenticationEntryPoint;
import com.communityplatform.users.security.JwtAuthenticationFilter;
import com.communityplatform.users.security.JwtTokenProvider;

import lombok.RequiredArgsConstructor;

/**
 * Spring Security configuration for JWT-based authentication.
 * 
 * MODIFIED FOR API GATEWAY:
 * When behind an API Gateway, the gateway handles JWT validation
 * and injects X-User-Id header. This config permits all requests
 * and relies on the gateway for authentication.
 * 
 * Configures:
 * - Disabled authentication (handled by gateway)
 * - Public access to all endpoints
 */
@Configuration
@EnableWebSecurity
// @EnableMethodSecurity // Disabled: API Gateway handles authorization
@RequiredArgsConstructor
public class SecurityConfig {

    private final CustomUserDetailsService userDetailsService;
    private final JwtAuthenticationEntryPoint authenticationEntryPoint;
    private final JwtAccessDeniedHandler accessDeniedHandler;
    private final JwtTokenProvider jwtTokenProvider;

    /**
     * Configure the security filter chain.
     * API Gateway handles authentication, so we permit all requests here.
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // Disable CSRF for stateless API
                .csrf(csrf -> csrf.disable())

                // Disable authentication - API Gateway handles it
                .authorizeHttpRequests(auth -> auth.anyRequest().permitAll())

                // Allow H2 console iframe
                .headers(headers -> headers
                        .frameOptions(frameOptions -> frameOptions.sameOrigin()));

        return http.build();
    }

    /**
     * Configure the authentication provider with our custom UserDetailsService.
     */
    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    /**
     * Password encoder using BCrypt.
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Authentication manager for programmatic authentication.
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }

    /**
     * Create JwtAuthenticationFilter bean.
     * This is manually created to avoid automatic servlet registration.
     */
    @Bean
    public JwtAuthenticationFilter jwtAuthenticationFilter() {
        return new JwtAuthenticationFilter(jwtTokenProvider, userDetailsService);
    }

    /**
     * Prevent JwtAuthenticationFilter from being registered as a servlet filter.
     * We only want it in the Spring Security filter chain, not as a global servlet
     * filter.
     */
    @Bean
    public FilterRegistrationBean<JwtAuthenticationFilter> jwtFilterRegistration(JwtAuthenticationFilter filter) {
        FilterRegistrationBean<JwtAuthenticationFilter> registration = new FilterRegistrationBean<>(filter);
        registration.setEnabled(false);
        return registration;
    }
}
