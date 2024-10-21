package io.github.muhammadredin.tokonyadiaapi.service.impl;

import io.github.muhammadredin.tokonyadiaapi.entity.Product;
import io.github.muhammadredin.tokonyadiaapi.entity.UserAccount;
import io.github.muhammadredin.tokonyadiaapi.service.AuthService;
import io.github.muhammadredin.tokonyadiaapi.service.ProductService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
@Slf4j
public class PermissionEvaluationServiceImpl {
    private final AuthService authService;
    private final ProductService productService;

    /**
     * Evaluates if the user has access to the specified store.
     *
     * @param id the store ID to check
     * @return true if the user has access to the store, false otherwise
     * @throws ResponseStatusException if access is denied due to null user account
     */
    public boolean storeServiceEval(String id) {
        UserAccount userAccount = authService.getAuthentication();

        try {
            boolean hasAccess = id.equals(userAccount.getStore().getId());
            log.info("Store access evaluation for user {}: {}", userAccount.getUsername(), hasAccess);
            return hasAccess;
        } catch (NullPointerException e) {
            log.error("Access denied: User account is null while evaluating store access");
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, HttpStatus.FORBIDDEN.getReasonPhrase());
        }
    }

    /**
     * Evaluates if the user has access to the specified customer account.
     *
     * @param id the customer ID to check
     * @return true if the user has access to the customer account, false otherwise
     * @throws ResponseStatusException if access is denied due to null user account
     */
    public boolean customerServiceEval(String id) {
        UserAccount userAccount = authService.getAuthentication();

        try {
            boolean hasAccess = id.equals(userAccount.getCustomer().getId());
            log.info("Customer access evaluation for user {}: {}", userAccount.getUsername(), hasAccess);
            return hasAccess;
        } catch (NullPointerException e) {
            log.error("Access denied: User account is null while evaluating customer access");
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, HttpStatus.FORBIDDEN.getReasonPhrase());
        }
    }

    /**
     * Evaluates if the user has access to the specified product.
     *
     * @param id the product ID to check
     * @return true if the user has access to the product, false otherwise
     * @throws ResponseStatusException if access is denied due to null user account
     */
    public boolean productServiceEval(String id) {
        UserAccount userAccount = authService.getAuthentication();
        Product product = productService.getOne(id);

        try {
            boolean hasAccess = product.getStore().getId().equals(userAccount.getStore().getId());
            log.info("Product access evaluation for user {}: {}", userAccount.getUsername(), hasAccess);
            return hasAccess;
        } catch (NullPointerException e) {
            log.error("Access denied: User account is null while evaluating product access");
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, HttpStatus.FORBIDDEN.getReasonPhrase());
        }
    }
}
