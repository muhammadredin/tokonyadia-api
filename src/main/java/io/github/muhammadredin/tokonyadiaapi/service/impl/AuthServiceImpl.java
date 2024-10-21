package io.github.muhammadredin.tokonyadiaapi.service.impl;

import io.github.muhammadredin.tokonyadiaapi.constant.UserResponseMessage;
import io.github.muhammadredin.tokonyadiaapi.dto.request.LoginRequest;
import io.github.muhammadredin.tokonyadiaapi.dto.response.LoginResponse;
import io.github.muhammadredin.tokonyadiaapi.entity.Customer;
import io.github.muhammadredin.tokonyadiaapi.entity.Store;
import io.github.muhammadredin.tokonyadiaapi.entity.UserAccount;
import io.github.muhammadredin.tokonyadiaapi.service.*;
import io.github.muhammadredin.tokonyadiaapi.util.ValidationUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthServiceImpl implements AuthService {
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final RefreshTokenService refreshTokenService;
    private final UserAccountService userAccountService;
    private final ValidationUtil validationUtil;

    @Override
    public LoginResponse login(LoginRequest request) {
        validationUtil.validate(request);
        try {
            log.info("Attempting login for credential: {}", request.getCredential());
            Authentication authentication = authenticationManager
                    .authenticate(new UsernamePasswordAuthenticationToken(request.getCredential(), request.getPassword()));

            log.info("Authentication successful for user: {}", authentication.getName());
            if (!authentication.isAuthenticated()) {
                log.warn("Authentication failed for credential: {}", request.getCredential());
                throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, UserResponseMessage.USER_LOGIN_ERROR);
            }

            UserAccount userAccount = (UserAccount) authentication.getPrincipal();
            String accessToken = jwtService.generateToken(userAccount);
            String refreshToken = refreshTokenService.generateRefreshToken(userAccount.getId());

            log.info("Access token and refresh token generated for user: {}", userAccount.getUsername());

            Optional<Customer> customer = Optional.ofNullable(userAccount.getCustomer());
            Optional<Store> store = Optional.ofNullable(userAccount.getStore());

            return LoginResponse.builder()
                    .accessToken(accessToken)
                    .refreshToken(refreshToken)
                    .role(userAccount.getRole().name())
                    .customerId(customer.map(Customer::getId).orElse(null))
                    .storeId(store.map(Store::getId).orElse(null))
                    .build();
        } catch (Exception e) {
            log.error("Error during login process for credential: {}. Error message: {}", request.getCredential(), e.getMessage(), e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

    @Override
    public LoginResponse refreshToken(String refreshToken) {
        log.info("Refreshing token: {}", refreshToken);
        try {
            String userId = refreshTokenService.getUserIdByRefreshToken(refreshToken);
            log.info("User ID associated with refresh token: {}", userId);

            String newRefreshToken = refreshTokenService.rotateRefreshToken(userId);
            UserAccount userAccount = userAccountService.getOne(userId);
            String accessToken = jwtService.generateToken(userAccount);

            log.info("New access token generated for user: {}", userAccount.getUsername());

            Optional<Customer> customer = Optional.ofNullable(userAccount.getCustomer());
            Optional<Store> store = Optional.ofNullable(userAccount.getStore());

            return LoginResponse.builder()
                    .accessToken(accessToken)
                    .refreshToken(newRefreshToken)
                    .role(userAccount.getRole().name())
                    .customerId(customer.map(Customer::getId).orElse(null))
                    .storeId(store.map(Store::getId).orElse(null))
                    .build();
        } catch (Exception e) {
            log.error("Error during token refresh for refreshToken: {}. Error message: {}", refreshToken, e.getMessage(), e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

    @Override
    public void logout(String bearerToken) {
        log.info("Logging out user with bearer token: {}", bearerToken);
        if (bearerToken == null) {
            log.warn("Bearer token is null during logout attempt");
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
        }

        try {
            UserAccount userAccount = (UserAccount) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            refreshTokenService.deleteRefreshToken(userAccount.getId());
            jwtService.blacklistToken(bearerToken);
            log.info("Logout successful for user: {}", userAccount.getUsername());
        } catch (Exception e) {
            log.error("Error during logout process. Error message: {}", e.getMessage(), e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

    @Override
    public UserAccount getAuthentication() {
        log.info("Retrieving authentication for the current user");
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserAccount account = (UserAccount) authentication.getPrincipal();
        log.info("Current authenticated user: {}", account.getUsername());
        return userAccountService.getOne(account.getId());
    }
}
