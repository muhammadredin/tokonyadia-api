package io.github.muhammadredin.tokonyadiaapi.service;

import org.springframework.transaction.annotation.Transactional;

public interface EmailService {
    @Transactional(rollbackFor = Exception.class)
    void sendPasswordResetEmail(String to, String resetUrl);
}
