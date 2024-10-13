package io.github.muhammadredin.tokonyadiaapi.service.impl;

import io.github.muhammadredin.tokonyadiaapi.entity.Product;
import io.github.muhammadredin.tokonyadiaapi.entity.UserAccount;
import io.github.muhammadredin.tokonyadiaapi.service.AuthService;
import io.github.muhammadredin.tokonyadiaapi.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class PermissionEvaluationServiceImpl {
    private final AuthService authService;
    private final ProductService productService;

    public boolean storeServiceEval(String id) {
        UserAccount userAccount = authService.getAuthentication();

        try {
            return id.equals(userAccount.getStore().getId());
        } catch (NullPointerException e) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, HttpStatus.FORBIDDEN.getReasonPhrase());
        }
    }

    public boolean customerServiceEval(String id) {
        UserAccount userAccount = authService.getAuthentication();

        try {
            return id.equals(userAccount.getCustomer().getId());
        } catch (NullPointerException e) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, HttpStatus.FORBIDDEN.getReasonPhrase());
        }
    }

    public boolean productServiceEval(String id) {
        UserAccount userAccount = authService.getAuthentication();
        Product product = productService.getOne(id);

        try {
            return product.getStore().getId().equals(userAccount.getStore().getId());
        } catch (NullPointerException e) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, HttpStatus.FORBIDDEN.getReasonPhrase());
        }
    }
}
