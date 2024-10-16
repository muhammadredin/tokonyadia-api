package io.github.muhammadredin.tokonyadiaapi.controller;

import io.github.muhammadredin.tokonyadiaapi.constant.APIPath;
import io.github.muhammadredin.tokonyadiaapi.constant.UserResponseMessage;
import io.github.muhammadredin.tokonyadiaapi.dto.request.LoginRequest;
import io.github.muhammadredin.tokonyadiaapi.dto.request.UserAccountRequest;
import io.github.muhammadredin.tokonyadiaapi.dto.response.LoginResponse;
import io.github.muhammadredin.tokonyadiaapi.service.AuthService;
import io.github.muhammadredin.tokonyadiaapi.service.UserAccountService;
import io.github.muhammadredin.tokonyadiaapi.util.ResponseUtil;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.Arrays;

@RestController
@RequestMapping(APIPath.USER_API)
@RequiredArgsConstructor
public class AuthController {
    private final UserAccountService userAccountService;
    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<?> loginHandler(@Valid @RequestBody LoginRequest loginRequest, HttpServletResponse response) {
        LoginResponse loginResponse = authService.login(loginRequest);
        setCookie(response, loginResponse.getRefreshToken());
        return ResponseUtil.buildResponse(HttpStatus.OK, UserResponseMessage.USER_LOGIN_SUCCESS, loginResponse);
    }

    @PostMapping("/refresh-token")
    public ResponseEntity<?> refreshTokenHandler(HttpServletRequest request, HttpServletResponse response) {
        String refreshToken = getRefreshTokenFromCookie(request);
        LoginResponse loginResponse = authService.refreshToken(refreshToken);
        setCookie(response, loginResponse.getRefreshToken());
        return ResponseUtil.buildResponse(HttpStatus.OK, UserResponseMessage.USER_LOGIN_SUCCESS, loginResponse);
    }

    @PostMapping("/register")
    public ResponseEntity<?> registerHandler(@Valid @RequestBody UserAccountRequest request) {
        userAccountService.createUserAccount(request);
        return ResponseUtil.buildResponse(HttpStatus.CREATED, UserResponseMessage.USER_REGISTER_SUCCESS, null);
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logoutHandler(HttpServletRequest request, HttpServletResponse response) {
        String bearerToken = request.getHeader(HttpHeaders.AUTHORIZATION);
        authService.logout(bearerToken);
        return ResponseUtil.buildResponse(HttpStatus.OK, UserResponseMessage.USER_LOGOUT_SUCCESS, null);
    }

    private String getRefreshTokenFromCookie(HttpServletRequest request) {
        Cookie cookie = Arrays.stream(request.getCookies())
                .filter(c -> c.getName().equals("refreshToken"))
                .findFirst()
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Refresh token required"));
        return cookie.getValue();
    }

    private void setCookie(HttpServletResponse response, String token) {
        Cookie cookie = new Cookie("refreshToken", token);
        cookie.setHttpOnly(true);
        cookie.setPath("/");
        cookie.setMaxAge(60 * 60 * 24);
        response.addCookie(cookie);
    }
}
