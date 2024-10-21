package io.github.muhammadredin.tokonyadiaapi.service.impl;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import io.github.muhammadredin.tokonyadiaapi.constant.JWTResponseMessage;
import io.github.muhammadredin.tokonyadiaapi.entity.UserAccount;
import io.github.muhammadredin.tokonyadiaapi.service.JwtService;
import io.github.muhammadredin.tokonyadiaapi.service.RedisService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.security.NoSuchAlgorithmException;
import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Base64;
import java.util.Date;

@Service
@RequiredArgsConstructor
@Slf4j
public class JwtServiceImpl implements JwtService {
    @Autowired
    private RedisService redisService;

    private final String SECRET_KEY;

    @Value("${tokonyadia.api.jwt-issuer}")
    private String ISSUER;

    public JwtServiceImpl() {
        try {
            KeyGenerator keyGen = KeyGenerator.getInstance("HmacSHA256");
            SecretKey secretKey = keyGen.generateKey();
            this.SECRET_KEY = Base64.getEncoder().encodeToString(secretKey.getEncoded());
        } catch (NoSuchAlgorithmException e) {
            log.error("Error generating secret key for JWT: {}", e.getMessage());
            throw new RuntimeException("Error generating secret key", e);
        }
    }

    @Override
    public String generateToken(UserAccount user) {
        try {
            Algorithm algorithm = Algorithm.HMAC256(SECRET_KEY.getBytes(StandardCharsets.UTF_8));
            String token = JWT.create()
                    .withIssuer(ISSUER)
                    .withSubject(user.getUsername())
                    .withIssuedAt(Instant.now())
                    .withExpiresAt(Instant.now().plus(5, ChronoUnit.MINUTES))
                    .withClaim("role", user.getRole().name())
                    .sign(algorithm);
            log.info("JWT Token generated for user: {}", user.getUsername());
            return token;
        } catch (JWTCreationException e) {
            log.error("Error generating JWT Token for user {}: {}", user.getUsername(), e.getMessage());
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

    @Override
    public void blacklistToken(String bearerToken) {
        String accessToken = parseToken(bearerToken);
        Date expDate = getExpDate(accessToken);
        long timeLeft = expDate.getTime() - System.currentTimeMillis();

        redisService.save("blacklistToken:" + accessToken, "BLACKLISTED", Duration.ofMillis(timeLeft));
        log.info("Token blacklisted successfully: {}", accessToken);
    }

    @Override
    public boolean validateToken(String token) {
        log.info("Validating JWT Token: {}", token);
        try {
            Algorithm algorithm = Algorithm.HMAC256(SECRET_KEY.getBytes(StandardCharsets.UTF_8));
            JWTVerifier verifier = JWT.require(algorithm)
                    .withIssuer(ISSUER)
                    .build();
            verifier.verify(token);
            log.info("JWT Token is valid.");
            return true;
        } catch (JWTVerificationException e) {
            log.error("Error validating JWT Token: {}", e.getMessage());
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, JWTResponseMessage.INVALID_JWT_ERROR);
        }
    }

    @Override
    public String getUserId(String token) {
        log.info("Extracting User ID from JWT Token: {}", token);
        try {
            Algorithm algorithm = Algorithm.HMAC256(SECRET_KEY.getBytes(StandardCharsets.UTF_8));
            JWTVerifier verifier = JWT.require(algorithm)
                    .withIssuer(ISSUER)
                    .build();
            DecodedJWT jwt = verifier.verify(token);
            log.info("User ID extracted from token: {}", jwt.getSubject());
            return jwt.getSubject();
        } catch (JWTVerificationException e) {
            log.error("Error extracting User ID from JWT Token: {}", e.getMessage());
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, JWTResponseMessage.INVALID_JWT_ERROR);
        }
    }

    @Override
    public Date getExpDate(String token) {
        log.info("Extracting Expiration Date from JWT Token: {}", token);
        try {
            Algorithm algorithm = Algorithm.HMAC256(SECRET_KEY.getBytes(StandardCharsets.UTF_8));
            JWTVerifier verifier = JWT.require(algorithm)
                    .withIssuer(ISSUER)
                    .build();
            DecodedJWT jwt = verifier.verify(token);
            log.info("Expiration date extracted: {}", jwt.getExpiresAt());
            return jwt.getExpiresAt();
        } catch (JWTVerificationException e) {
            log.error("Error extracting expiration date from JWT Token: {}", e.getMessage());
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, JWTResponseMessage.INVALID_JWT_ERROR);
        }
    }

    private String parseToken(String bearerToken) {
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            String token = bearerToken.substring(7);
            log.info("Parsed token: {}", token);
            return token;
        }
        log.warn("Invalid bearer token format: {}", bearerToken);
        throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, JWTResponseMessage.INVALID_JWT_ERROR);
    }
}
