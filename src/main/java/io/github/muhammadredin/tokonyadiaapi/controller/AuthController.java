package io.github.muhammadredin.tokonyadiaapi.controller;

import io.github.muhammadredin.tokonyadiaapi.constant.APIPath;
import io.github.muhammadredin.tokonyadiaapi.constant.UserResponseMessage;
import io.github.muhammadredin.tokonyadiaapi.dto.request.LoginRequest;
import io.github.muhammadredin.tokonyadiaapi.dto.request.UserAccountRequest;
import io.github.muhammadredin.tokonyadiaapi.dto.response.CommonResponse;
import io.github.muhammadredin.tokonyadiaapi.dto.response.LoginResponse;
import io.github.muhammadredin.tokonyadiaapi.service.AuthService;
import io.github.muhammadredin.tokonyadiaapi.service.UserAccountService;
import io.github.muhammadredin.tokonyadiaapi.util.ResponseUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.Arrays;

@RestController
@RequestMapping(APIPath.USER_API)
@RequiredArgsConstructor
@Tag(name = "Authentication", description = "APIs for user authentication, token refresh, and logout")
public class AuthController {
    private static class CommonResponseAuthResponse extends CommonResponse<LoginResponse> {}

    private final AuthService authService;

    @Operation(summary = "User login",
            description = "Login to the system using valid credentials. A refresh token will be set in the cookies upon successful login.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Login successful", content = @Content(schema = @Schema(implementation = CommonResponseAuthResponse.class))),
                    @ApiResponse(responseCode = "401", description = "Invalid login credentials", content = @Content(schema = @Schema(implementation = CommonResponse.class)))
            })
    @PostMapping("/login")
    public ResponseEntity<?> loginHandler(@Valid @RequestBody LoginRequest loginRequest, HttpServletResponse response) {
        LoginResponse loginResponse = authService.login(loginRequest);
        setCookie(response, loginResponse.getRefreshToken());
        return ResponseUtil.buildResponse(HttpStatus.OK, UserResponseMessage.USER_LOGIN_SUCCESS, loginResponse);
    }

    @Operation(summary = "Refresh token",
            description = "Generate a new access token using the refresh token stored in cookies. A new refresh token will be set in the cookies as well.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Token refreshed successfully", content = @Content(schema = @Schema(implementation = CommonResponseAuthResponse.class))),
                    @ApiResponse(responseCode = "400", description = "Invalid or missing refresh token", content = @Content(schema = @Schema(implementation = CommonResponse.class))),
                    @ApiResponse(responseCode = "401", description = "Unauthorized access", content = @Content(schema = @Schema(implementation = CommonResponse.class)))
            })
    @PostMapping("/refresh-token")
    public ResponseEntity<?> refreshTokenHandler(HttpServletRequest request, HttpServletResponse response) {
        String refreshToken = getRefreshTokenFromCookie(request);
        LoginResponse loginResponse = authService.refreshToken(refreshToken);
        setCookie(response, loginResponse.getRefreshToken());
        return ResponseUtil.buildResponse(HttpStatus.OK, UserResponseMessage.USER_LOGIN_SUCCESS, loginResponse);
    }

    @Operation(summary = "Logout",
            description = "Log the user out by invalidating the current access token. No content is returned on successful logout.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Logout successful"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized access", content = @Content(schema = @Schema(implementation = CommonResponse.class)))
            })
    @SecurityRequirement(name = "Bearer Authentication")
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
