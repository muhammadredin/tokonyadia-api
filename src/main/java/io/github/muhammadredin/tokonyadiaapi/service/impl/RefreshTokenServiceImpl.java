package io.github.muhammadredin.tokonyadiaapi.service.impl;

import io.github.muhammadredin.tokonyadiaapi.service.RedisService;
import io.github.muhammadredin.tokonyadiaapi.service.RefreshTokenService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.Duration;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j  // Enable SLF4J logging for this class
public class RefreshTokenServiceImpl implements RefreshTokenService {

    private final RedisService redisService;

    @Value("${tokonyadia.api.refresh-token-expiration-in-hours}")
    private Integer DURATION;

    @Override
    public String generateRefreshToken(String userId) {
        // Generate a new refresh token
        String refreshToken = UUID.randomUUID().toString();
        log.info("Generating refresh token for user ID: {}", userId);

        // Check if there's an existing refresh token for the user
        String oldRefreshToken = redisService.get("refreshToken:" + userId);
        if (oldRefreshToken != null) {
            log.info("Deleting old refresh token for user ID: {}", userId);
            deleteRefreshToken(userId); // Delete the old token if it exists
        }

        // Save the new refresh token in Redis
        redisService.save("refreshToken:" + userId, refreshToken, Duration.ofHours(DURATION));
        redisService.save("refreshTokenMap:" + refreshToken, userId, Duration.ofHours(DURATION));

        log.info("Successfully generated and saved refresh token for user ID: {}", userId);
        return refreshToken;
    }

    @Override
    public void deleteRefreshToken(String userId) {
        log.info("Deleting refresh token for user ID: {}", userId);
        String refreshToken = redisService.get("refreshToken:" + userId);

        // Delete the refresh token and its mapping from Redis
        redisService.delete("refreshToken:" + userId);
        redisService.delete("refreshTokenMap:" + refreshToken);

        log.info("Successfully deleted refresh token for user ID: {}", userId);
    }

    @Override
    public String rotateRefreshToken(String userId) {
        log.info("Rotating refresh token for user ID: {}", userId);
        deleteRefreshToken(userId); // Delete the old refresh token
        return generateRefreshToken(userId); // Generate a new one
    }

    @Override
    public String getUserIdByRefreshToken(String refreshToken) {
        log.info("Retrieving user ID for refresh token: {}", refreshToken);
        String userId = redisService.get("refreshTokenMap:" + refreshToken);

        // Check if the user ID is found
        if (userId == null) {
            log.warn("Token not found or expired for refresh token: {}", refreshToken);
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Token not found, could be expired");
        }

        log.info("Successfully retrieved user ID: {} for refresh token: {}", userId, refreshToken);
        return userId;
    }
}
