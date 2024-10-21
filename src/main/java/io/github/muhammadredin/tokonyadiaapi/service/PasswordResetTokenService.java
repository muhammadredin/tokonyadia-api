package io.github.muhammadredin.tokonyadiaapi.service;

public interface PasswordResetTokenService {
    String generatePasswordResetToken(String userId);

    void deletePasswordResetToken(String userId);

    String getUserIdByPasswordResetToken(String passwordResetToken);

    boolean isPasswordResetTokenValid(String userId, String passwordResetToken);
}
