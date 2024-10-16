package io.github.muhammadredin.tokonyadiaapi.service;

public interface RefreshTokenService {
    String generateRefreshToken(String userId);

    void deleteRefreshToken(String userId);

    String rotateRefreshToken(String userId);

    String getUserIdByRefreshToken(String refreshToken);
}
