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
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class CartServiceImpl implements CartService {
    private final CartRepository cartRepository;
    private final ProductService productService;
    private final ValidationUtil validationUtil;

    @Transactional(readOnly = true)
    @Override
    public List<CartResponse> getAllProduct() {
        UserAccount userAccount = (UserAccount) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        Set<Cart> cart = userAccount.getCustomer().getCart();
        return cart.stream().map(this::toCartResponse).toList();
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void addProductToCart(CartRequest request) {
        validationUtil.validate(request);
        UserAccount userAccount = (UserAccount) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Customer customer = userAccount.getCustomer();
        Product product = productService.getOne(request.getProductId());

        checkQuantityToStock(request.getQuantity(), product.getStock());
        Set<Cart> checkCustomerCart = customer.getCart();

        for (Cart cart : checkCustomerCart) {
            if (cart.getProduct().getId().equals(product.getId())) {
                checkQuantityToStock((request.getQuantity() + cart.getQuantity()), product.getStock());
                cart.setQuantity(cart.getQuantity() + request.getQuantity());
                cartRepository.save(cart);
                return;
            }
        }

        Cart cart = Cart.builder()
                .product(product)
                .customer(customer)
                .quantity(request.getQuantity())
                .build();
        cartRepository.save(cart);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void updateProductQuantity(String cartId, CartUpdateProductQuantityRequest request) {
        validationUtil.validate(request);
        Cart cart = getOne(cartId);
        checkQuantityToStock(request.getQuantity(), cart.getProduct().getStock());
        cart.setQuantity(request.getQuantity());
        cartRepository.save(cart);
    }

    @Transactional(readOnly = true)
    @Override
    public Cart getOne(String id) {
        return cartRepository.findById(id).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, String.format(CartResponseMessage.ERROR_CART_NOT_FOUND, id)));
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void deleteCart(Cart cart) {
        cartRepository.delete(cart);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void deleteCartById(String cartId) {
        UserAccount userAccount = (UserAccount) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        Cart cart = getOne(cartId);
        if (cart.getCustomer() != userAccount.getCustomer())
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, String.format(CartResponseMessage.ERROR_CART_NOT_FOUND, cartId));

        cartRepository.delete(getOne(cartId));
    }

    private void checkQuantityToStock(Integer quantity, Integer stock) {
        if (quantity > stock)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, CartResponseMessage.ERROR_INSUFFICIENT_PRODUCT_ADD_TO_CART);
    }

    private CartResponse toCartResponse(Cart cart) {
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
