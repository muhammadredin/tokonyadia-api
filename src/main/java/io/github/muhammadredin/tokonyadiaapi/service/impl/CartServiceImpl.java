package io.github.muhammadredin.tokonyadiaapi.service.impl;

import io.github.muhammadredin.tokonyadiaapi.dto.request.CartRequest;
import io.github.muhammadredin.tokonyadiaapi.dto.request.CartUpdateProductQuantityRequest;
import io.github.muhammadredin.tokonyadiaapi.dto.response.CartResponse;
import io.github.muhammadredin.tokonyadiaapi.entity.Cart;
import io.github.muhammadredin.tokonyadiaapi.entity.Product;
import io.github.muhammadredin.tokonyadiaapi.repository.CartRepository;
import io.github.muhammadredin.tokonyadiaapi.service.CartService;
import io.github.muhammadredin.tokonyadiaapi.service.CustomerService;
import io.github.muhammadredin.tokonyadiaapi.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class CartServiceImpl implements CartService {
    private final CartRepository cartRepository;
    private final ProductService productService;
    private final CustomerService customerService;

    @Override
    public List<CartResponse> getAllProduct(String id) {
        Set<Cart> cart = customerService.getOne(id).getCart();
        return cart.stream().map(this::toCartResponse).toList();
    }

    @Override
    public void addProductToCart(String id, CartRequest request) {
        Product product = productService.getOne(request.getProductId());
        checkQuantityToStock(request.getQuantity(), product.getStock());
        Cart cart = Cart.builder()
                .product(product)
                .customer(customerService.getOne(id))
                .quantity(request.getQuantity())
                .build();
        cartRepository.save(cart);
    }

    @Override
    public void updateProductQuantity(String id, String cartId, CartUpdateProductQuantityRequest request) {
        Cart cart = getOne(cartId);
        checkQuantityToStock(request.getQuantity(), cart.getProduct().getStock());
        cart.setQuantity(request.getQuantity());
        cartRepository.save(cart);
    }

    @Override
    public Cart getOne(String id){
        return cartRepository.findById(id).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, String.format("Cart with id %s not found", id)));
    }

    @Override
    public void deleteCart(String id) {
        Cart cart = getOne(id);
        cartRepository.delete(cart);
    }

    private void checkQuantityToStock(Integer quantity, Integer stock) {
        if (quantity > stock) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Quantity is greater than stock");
    }

    private CartResponse toCartResponse(Cart cart) {
        return CartResponse.builder()
                .cartId(cart.getId())
                .id(cart.getProduct().getId())
                .name(cart.getProduct().getName())
                .description(cart.getProduct().getDescription())
                .price(cart.getProduct().getPrice())
                .stock(cart.getProduct().getStock())
                .quantity(cart.getQuantity())
                .storeName(cart.getProduct().getStore().getName())
                .build();
    }
}
