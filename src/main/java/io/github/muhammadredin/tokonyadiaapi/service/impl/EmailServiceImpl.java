package io.github.muhammadredin.tokonyadiaapi.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class EmailServiceImpl {
    private final JavaMailSender mailSender;

    @Transactional(rollbackFor = Exception.class)
    public void sendPasswordResetEmail(String to, String resetUrl) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject("Password Reset Request");
        message.setText(resetUrl);
        mailSender.send(message);
    }
}
