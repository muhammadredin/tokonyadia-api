package io.github.muhammadredin.tokonyadiaapi.service;

import io.github.muhammadredin.tokonyadiaapi.dto.request.LoginRequest;
import io.github.muhammadredin.tokonyadiaapi.dto.response.LoginResponse;

public interface AuthService {
    LoginResponse login(LoginRequest request);
}
