package io.github.muhammadredin.tokonyadiaapi.service.impl;

import io.github.muhammadredin.tokonyadiaapi.service.PasswordResetTokenService;
import io.github.muhammadredin.tokonyadiaapi.service.RedisService;
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
@Slf4j
public class PasswordResetTokenServiceImpl implements PasswordResetTokenService {
    private final RedisService redisService;

    @Value("${tokonyadia.api.password-reset-token-expiration-in-hours}")
    private Integer DURATION;

    @Override
    public String generatePasswordResetToken(String userId) {
        // Generate a new password reset token
        String passwordResetToken = UUID.randomUUID().toString();
        log.info("Generating password reset token for user ID: {}", userId);

        // Check if there's an existing password reset token for the user
        String oldRefreshToken = redisService.get("passwordResetToken:" + userId);
        if (oldRefreshToken != null) {
            log.info("Deleting old password reset token for user ID: {}", userId);
            deletePasswordResetToken(userId); // Delete the old token if it exists
        }

        // Save the new password reset token in Redis
        redisService.save("passwordResetToken:" + userId, passwordResetToken, Duration.ofHours(DURATION));
        redisService.save("passwordResetTokenMap:" + passwordResetToken, userId, Duration.ofHours(DURATION));

        log.info("Successfully generated and saved password reset token for user ID: {}", userId);
        return passwordResetToken;
    }

    @Override
    public void deletePasswordResetToken(String userId) {
        log.info("Deleting password reset token for user ID: {}", userId);
        String passwordResetToken = redisService.get("passwordResetToken:" + userId);

        // Delete the password reset token and its mapping from Redis
        redisService.delete("passwordResetToken:" + userId);
        redisService.delete("passwordResetTokenMap:" + passwordResetToken);

        log.info("Successfully deleted password reset token for user ID: {}", userId);
    }

    @Override
    public String getUserIdByPasswordResetToken(String passwordResetToken) {
        log.info("Retrieving user ID for password reset token: {}", passwordResetToken);
        String userId = redisService.get("passwordResetTokenMap:" + passwordResetToken);

        // Check if the user ID is found
        if (userId == null) {
            log.warn("Token not found or expired for password reset token: {}", passwordResetToken);
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Token not found, could be expired");
        }

        log.info("Successfully retrieved user ID: {} for password reset token: {}", userId, passwordResetToken);
        return userId;
    }

    @Override
    public boolean isPasswordResetTokenValid(String userId, String requestToken) {
        log.info("Retrieving password reset token for user ID: {}", userId);
        String passwordResetToken = redisService.get("passwordResetToken:" + userId);
        return passwordResetToken != null && passwordResetToken.equals(requestToken);
    }
}
