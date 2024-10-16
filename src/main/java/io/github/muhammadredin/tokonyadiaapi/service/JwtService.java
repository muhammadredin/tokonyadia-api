package io.github.muhammadredin.tokonyadiaapi.service;

import io.github.muhammadredin.tokonyadiaapi.entity.UserAccount;
import org.springframework.security.core.userdetails.UserDetails;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.function.Function;

public interface JwtService {

    String generateToken(UserAccount user);

    void blacklistToken(String bearerToken);

    boolean validateToken(String token);

    String getUserId(String token);

    Date getExpDate(String token);
}
