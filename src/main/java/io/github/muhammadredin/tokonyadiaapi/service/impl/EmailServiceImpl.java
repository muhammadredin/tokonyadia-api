package io.github.muhammadredin.tokonyadiaapi.service.impl;

import io.github.muhammadredin.tokonyadiaapi.service.EmailService;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class EmailServiceImpl implements EmailService {
    private final JavaMailSender mailSender;

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void sendPasswordResetEmail(String to, String resetUrl) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject("Password Reset Request");
        message.setText(resetUrl);
        mailSender.send(message);
    }
}
