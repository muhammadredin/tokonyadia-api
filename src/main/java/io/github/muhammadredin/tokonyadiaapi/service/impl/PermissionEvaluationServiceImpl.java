package io.github.muhammadredin.tokonyadiaapi.service.impl;

import io.github.muhammadredin.tokonyadiaapi.entity.UserAccount;
import io.github.muhammadredin.tokonyadiaapi.service.AuthService;
import io.github.muhammadredin.tokonyadiaapi.service.CustomerService;
import io.github.muhammadredin.tokonyadiaapi.service.StoreService;
import io.github.muhammadredin.tokonyadiaapi.service.UserAccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PermissionEvaluationServiceImpl {
    private final StoreService storeService;
    private final CustomerService customerService;
    private final AuthService authService;

    public boolean storeServiceEval(String id) {
        UserAccount userAccount = authService.getAuthentication();

        try {
            return id.equals(userAccount.getStore().getId());
        } catch (NullPointerException e) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Access Denied");
        }
    }

    public boolean customerServiceEval(String id) {
        UserAccount userAccount = authService.getAuthentication();

        try {
            return id.equals(userAccount.getCustomer().getId());
        } catch (NullPointerException e) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Access Denied");
        }
    }
}
