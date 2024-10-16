package io.github.muhammadredin.tokonyadiaapi.service.impl;

import io.github.muhammadredin.tokonyadiaapi.service.RedisService;
import io.github.muhammadredin.tokonyadiaapi.service.RefreshTokenService;
import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.Duration;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RefreshTokenServiceImpl implements RefreshTokenService {

    private final RedisService redisService;

    @Value("${tokonyadia.api.refresh-token-expiration-in-hours}")
    private Integer DURATION;

    @Override
    public String generateRefreshToken(String userId) {
        String refreshToken = UUID.randomUUID().toString();

        String oldRefreshToken = redisService.get("refreshToken:" + userId);

        if (oldRefreshToken != null) {
            deleteRefreshToken(userId);
        }

        redisService.save("refreshToken:" + userId, refreshToken, Duration.ofHours(DURATION));
        redisService.save("refreshTokenMap:" + refreshToken, userId, Duration.ofHours(DURATION));
        return refreshToken;
    }

    @Override
    public void deleteRefreshToken(String userId) {
        String refreshToken = redisService.get("refreshToken:" + userId);
        redisService.delete("refreshToken:" + userId);
        redisService.delete("refreshTokenMap:" + refreshToken);
    }

    @Override
    public String rotateRefreshToken(String userId) {
        deleteRefreshToken(userId);
        return generateRefreshToken(userId);
    }

    @Override
    public String getUserIdByRefreshToken(String refreshToken) {
        String userId = redisService.get("refreshTokenMap:" + refreshToken);
        if (userId == null) throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Token not found, could be expired");
        return userId;
    }
}
