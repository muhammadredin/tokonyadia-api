package io.github.muhammadredin.tokonyadiaapi.service;

import io.github.muhammadredin.tokonyadiaapi.dto.request.LoginRequest;
import io.github.muhammadredin.tokonyadiaapi.dto.response.LoginResponse;
import io.github.muhammadredin.tokonyadiaapi.entity.UserAccount;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public interface AuthService {
    LoginResponse login(LoginRequest request);

    LoginResponse refreshToken(String refreshToken);

    void logout(String bearerToken);

    UserAccount getAuthentication();
}
