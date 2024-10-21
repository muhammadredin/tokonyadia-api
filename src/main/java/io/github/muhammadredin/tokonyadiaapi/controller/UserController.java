package io.github.muhammadredin.tokonyadiaapi.controller;

import io.github.muhammadredin.tokonyadiaapi.constant.UserResponseMessage;
import io.github.muhammadredin.tokonyadiaapi.dto.request.ForgotPasswordRequest;
import io.github.muhammadredin.tokonyadiaapi.dto.request.PasswordResetRequest;
import io.github.muhammadredin.tokonyadiaapi.dto.request.UserAccountRequest;
import io.github.muhammadredin.tokonyadiaapi.service.UserAccountService;
import io.github.muhammadredin.tokonyadiaapi.util.ResponseUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {
    private final UserAccountService userAccountService;

    @PostMapping("/register")
    public ResponseEntity<?> registerHandler(@Valid @RequestBody UserAccountRequest request) {
        userAccountService.createUserAccount(request);
        return ResponseUtil.buildResponse(HttpStatus.CREATED, UserResponseMessage.USER_REGISTER_SUCCESS, null);
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<?> forgotPasswordHandler(@Valid @RequestBody ForgotPasswordRequest request) {

        return ResponseUtil.buildResponse(HttpStatus.OK, UserResponseMessage.USER_FORGOT_PASSWORD_SUCCESS, userAccountService.createPasswordResetRequest(request));
    }

    @PutMapping("/reset-password")
    public ResponseEntity<?> resetPasswordHandler(@Valid @RequestBody PasswordResetRequest request) {
        userAccountService.passwordReset(request);
        return ResponseUtil.buildResponse(HttpStatus.OK, UserResponseMessage.USER_CHANGE_PASSWORD_SUCCESS, null);
    }
}
