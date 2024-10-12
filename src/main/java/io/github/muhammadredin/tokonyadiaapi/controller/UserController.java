package io.github.muhammadredin.tokonyadiaapi.controller;

import io.github.muhammadredin.tokonyadiaapi.constant.APIPath;
import io.github.muhammadredin.tokonyadiaapi.constant.UserResponseMessage;
import io.github.muhammadredin.tokonyadiaapi.dto.request.LoginRequest;
import io.github.muhammadredin.tokonyadiaapi.dto.request.UserAccountRequest;
import io.github.muhammadredin.tokonyadiaapi.service.AuthService;
import io.github.muhammadredin.tokonyadiaapi.service.UserAccountService;
import io.github.muhammadredin.tokonyadiaapi.util.ResponseUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(APIPath.USER_API)
@RequiredArgsConstructor
public class UserController {
    private final UserAccountService userAccountService;
    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<?> loginHandler(@RequestBody LoginRequest request) {
        return ResponseUtil.buildResponse(HttpStatus.OK, UserResponseMessage.USER_LOGIN_SUCCESS, authService.login(request));
    }

    @PostMapping("/register")
    public ResponseEntity<?> registerHandler(@RequestBody UserAccountRequest request) {
        userAccountService.createUserAccount(request);
        return ResponseUtil.buildResponse(HttpStatus.CREATED, UserResponseMessage.USER_REGISTER_SUCCESS, null);
    }
}
