package io.github.muhammadredin.tokonyadiaapi.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.client.RestTemplate;

import java.util.Properties;

@Configuration
public class BeanConfiguration {

    @Value("${spring.mail.host}")
    public String MAIL_HOST;

    @Value("${spring.mail.port}")
    public Integer MAIL_PORT;

    @Value("${spring.mail.username}")
    public String MAIL_USERNAME;

    @Value("${spring.mail.password}")
    public String MAIL_PASSWORD;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public ObjectMapper objectMapper() {
        return new ObjectMapper().registerModule(new JavaTimeModule());
    }

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

    @Bean
    public JavaMailSender mailSender() {
        JavaMailSenderImpl sender = new JavaMailSenderImpl();
        sender.setHost(MAIL_HOST);
        sender.setPort(MAIL_PORT);

        sender.setUsername(MAIL_USERNAME);
        sender.setPassword(MAIL_PASSWORD);

        Properties props = sender.getJavaMailProperties();
        props.put("mail.transport.protocol", "smtp");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.debug", "true");

        return sender;
    }
}
