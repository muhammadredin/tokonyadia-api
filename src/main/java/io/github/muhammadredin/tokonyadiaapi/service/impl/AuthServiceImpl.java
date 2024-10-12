package io.github.muhammadredin.tokonyadiaapi.service.impl;

import io.github.muhammadredin.tokonyadiaapi.constant.UserResponseMessage;
import io.github.muhammadredin.tokonyadiaapi.dto.request.LoginRequest;
import io.github.muhammadredin.tokonyadiaapi.dto.response.LoginResponse;
import io.github.muhammadredin.tokonyadiaapi.entity.Customer;
import io.github.muhammadredin.tokonyadiaapi.entity.Store;
import io.github.muhammadredin.tokonyadiaapi.entity.UserAccount;
import io.github.muhammadredin.tokonyadiaapi.service.AuthService;
import io.github.muhammadredin.tokonyadiaapi.service.JwtService;
import io.github.muhammadredin.tokonyadiaapi.service.UserAccountService;
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
    private final UserAccountService userAccountService;

    @Override
    public LoginResponse login(LoginRequest request) {
        try {
            log.info("Login request: {}", request);
            Authentication authentication = authenticationManager
                    .authenticate(new UsernamePasswordAuthenticationToken(request.getCredential(), request.getPassword()));
            log.info("Authentication success: {}", authentication);
            if (!authentication.isAuthenticated()) throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, UserResponseMessage.USER_LOGIN_ERROR);

            UserAccount userAccount = (UserAccount) authentication.getPrincipal();
            String token = jwtService.generateToken(userAccount);

            Optional<Customer> customer = Optional.ofNullable(userAccount.getCustomer());
            Optional<Store> store = Optional.ofNullable(userAccount.getStore());

            return LoginResponse.builder()
                    .token(token)
                    .role(userAccount.getRole().name())
                    .customerId(customer.map(Customer::getId).orElse(null))
                    .storeId(store.map(Store::getId).orElse(null))
                    .build();
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, e.getMessage());
        }
    }

    @Override
    public UserAccount getAuthentication() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserAccount account = (UserAccount) authentication.getPrincipal();
        return userAccountService.getOne(account.getId());
    }
}
