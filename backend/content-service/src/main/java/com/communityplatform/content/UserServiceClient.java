package com.communityplatform.content;

import java.time.Duration;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceClient {

    private final WebClient userServiceWebClient;

    @Value("${user-service.timeout-ms:2000}")
    private long timeoutMs;

    public Optional<UserProfileDto> getUserById(Long userId) {
        if (userId == null) {
            return Optional.empty();
        }

        try {
            UserProfileDto profile = userServiceWebClient.get()
                    .uri("/api/v1/users/{id}", userId)
                    .retrieve()
                    .bodyToMono(UserProfileDto.class)
                    .timeout(Duration.ofMillis(timeoutMs))
                    .block();
            return Optional.ofNullable(profile);
        } catch (Exception ex) {
            log.warn("Failed to fetch user {} from user-service: {}", userId, ex.getMessage());
            return Optional.empty();
        }
    }
}
