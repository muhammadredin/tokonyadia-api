package io.github.muhammadredin.tokonyadiaapi.service;

import io.github.muhammadredin.tokonyadiaapi.dto.request.CartRequest;
import io.github.muhammadredin.tokonyadiaapi.dto.request.CartUpdateProductQuantityRequest;
import io.github.muhammadredin.tokonyadiaapi.dto.response.CartResponse;
import io.github.muhammadredin.tokonyadiaapi.entity.Cart;

import java.util.List;

public interface CartService {
    List<CartResponse> getAllProduct(String id);

    void addProductToCart(String id, CartRequest request);

    void updateProductQuantity(String id, String cartId, CartUpdateProductQuantityRequest request);

    Cart getOne(String id);

    void deleteCart(String id);
}
