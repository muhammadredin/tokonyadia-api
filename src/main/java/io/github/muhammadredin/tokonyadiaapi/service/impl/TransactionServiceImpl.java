package io.github.muhammadredin.tokonyadiaapi.service.impl;

import io.github.muhammadredin.tokonyadiaapi.constant.TransactionStatus;
import io.github.muhammadredin.tokonyadiaapi.dto.request.TransactionRequest;
import io.github.muhammadredin.tokonyadiaapi.entity.Cart;
import io.github.muhammadredin.tokonyadiaapi.entity.Transaction;
import io.github.muhammadredin.tokonyadiaapi.entity.UserAccount;
import io.github.muhammadredin.tokonyadiaapi.repository.TransactionRepository;
import io.github.muhammadredin.tokonyadiaapi.service.AuthService;
import io.github.muhammadredin.tokonyadiaapi.service.CartService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TransactionServiceImpl {
    private TransactionRepository transactionRepository;
    private AuthService authService;
    private CartService cartService;

    @Transactional(rollbackFor = Exception.class)
    public void createTransaction(TransactionRequest request) {
        UserAccount authentication = authService.getAuthentication();
        List<Cart> carts = new ArrayList<>();

        for (String cartId : request.getCarts()) {
            Cart cart = cartService.getOne(cartId);
            if (!cart.getCustomer().getId().equals(authentication.getCustomer().getId()))
                throw new ResponseStatusException(HttpStatus.FORBIDDEN);

            carts.add(cart);
        }

        Transaction transaction = Transaction.builder()
                .transactionStatus(TransactionStatus.PENDING)
                .customer(authentication.getCustomer())
                .build();
    }
}
