package io.github.muhammadredin.tokonyadiaapi.service.impl;

import io.github.muhammadredin.tokonyadiaapi.constant.CartResponseMessage;
import io.github.muhammadredin.tokonyadiaapi.dto.request.CartRequest;
import io.github.muhammadredin.tokonyadiaapi.dto.request.CartUpdateProductQuantityRequest;
import io.github.muhammadredin.tokonyadiaapi.dto.response.CartResponse;
import io.github.muhammadredin.tokonyadiaapi.entity.*;
import io.github.muhammadredin.tokonyadiaapi.repository.CartRepository;
import io.github.muhammadredin.tokonyadiaapi.service.CartService;
import io.github.muhammadredin.tokonyadiaapi.service.ProductService;
import io.github.muhammadredin.tokonyadiaapi.util.ValidationUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Slf4j
public class CartServiceImpl implements CartService {
    private final CartRepository cartRepository;
    private final ProductService productService;
    private final ValidationUtil validationUtil;

    @Transactional(readOnly = true)
    @Override
    public List<CartResponse> getAllProduct() {
        UserAccount userAccount = (UserAccount) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        log.info("Fetching all products for user: {}", userAccount.getUsername());

        Set<Cart> cart = userAccount.getCustomer().getCart();
        log.info("User {} has {} items in their cart", userAccount.getUsername(), cart.size());

        return cart.stream().map(this::toCartResponse).toList();
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void addProductToCart(CartRequest request) {
        log.info("Adding product with ID: {} to cart for user: {}", request.getProductId(), SecurityContextHolder.getContext().getAuthentication().getName());
        validationUtil.validate(request);

        UserAccount userAccount = (UserAccount) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Customer customer = userAccount.getCustomer();
        Product product = productService.getOne(request.getProductId());

        checkQuantityToStock(request.getQuantity(), product.getStock());
        log.info("Product ID: {} has sufficient stock. Adding {} units to cart", product.getId(), request.getQuantity());

        Set<Cart> checkCustomerCart = customer.getCart();

        for (Cart cart : checkCustomerCart) {
            if (cart.getProduct().getId().equals(product.getId())) {
                log.info("Product ID: {} already in cart, updating quantity", product.getId());
                checkQuantityToStock((request.getQuantity() + cart.getQuantity()), product.getStock());
                cart.setQuantity(cart.getQuantity() + request.getQuantity());
                cartRepository.save(cart);
                log.info("Updated quantity of product ID: {} to {}", product.getId(), cart.getQuantity());
                return;
            }
        }

        Cart cart = Cart.builder()
                .product(product)
                .customer(customer)
                .quantity(request.getQuantity())
                .build();
        cartRepository.save(cart);
        log.info("Added new product ID: {} to cart for user: {}", product.getId(), userAccount.getUsername());
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void updateProductQuantity(String cartId, CartUpdateProductQuantityRequest request) {
        log.info("Updating product quantity in cart with ID: {}", cartId);
        validationUtil.validate(request);

        Cart cart = getOne(cartId);
        checkQuantityToStock(request.getQuantity(), cart.getProduct().getStock());
        cart.setQuantity(request.getQuantity());
        cartRepository.save(cart);
        log.info("Updated product quantity in cart ID: {} to {}", cartId, request.getQuantity());
    }

    @Transactional(readOnly = true)
    @Override
    public Cart getOne(String id) {
        log.info("Fetching cart with ID: {}", id);
        return cartRepository.findById(id).orElseThrow(
                () -> {
                    log.error("Cart with ID: {} not found", id);
                    return new ResponseStatusException(HttpStatus.NOT_FOUND, String.format(CartResponseMessage.ERROR_CART_NOT_FOUND, id));
                });
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void deleteCart(Cart cart) {
        log.info("Deleting cart with ID: {}", cart.getId());
        cartRepository.delete(cart);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void deleteCartById(String cartId) {
        log.info("Deleting cart with ID: {}", cartId);
        UserAccount userAccount = (UserAccount) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        Cart cart = getOne(cartId);
        if (!cart.getCustomer().equals(userAccount.getCustomer())) {
            log.error("Cart with ID: {} does not belong to user: {}", cartId, userAccount.getUsername());
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, String.format(CartResponseMessage.ERROR_CART_NOT_FOUND, cartId));
        }

        cartRepository.delete(cart);
        log.info("Cart with ID: {} successfully deleted", cartId);
    }

    private void checkQuantityToStock(Integer quantity, Integer stock) {
        if (quantity > stock) {
            log.warn("Requested quantity: {} exceeds available stock: {}", quantity, stock);
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, CartResponseMessage.ERROR_INSUFFICIENT_PRODUCT_ADD_TO_CART);
        }
    }

    private CartResponse toCartResponse(Cart cart) {
        log.info("Converting cart ID: {} to CartResponse", cart.getId());
        return CartResponse.builder()
                .cartId(cart.getId())
                .productId(cart.getProduct().getId())
                .name(cart.getProduct().getName())
                .description(cart.getProduct().getDescription())
                .price(cart.getProduct().getPrice())
                .stock(cart.getProduct().getStock())
                .quantity(cart.getQuantity())
                .storeName(cart.getProduct().getStore().getName())
                .build();
    }
}
