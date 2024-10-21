package io.github.muhammadredin.tokonyadiaapi.controller;

import io.github.muhammadredin.tokonyadiaapi.constant.APIPath;
import io.github.muhammadredin.tokonyadiaapi.constant.CustomerResponseMessage;
import io.github.muhammadredin.tokonyadiaapi.dto.request.CartRequest;
import io.github.muhammadredin.tokonyadiaapi.dto.request.CartUpdateProductQuantityRequest;
import io.github.muhammadredin.tokonyadiaapi.service.CartService;
import io.github.muhammadredin.tokonyadiaapi.util.ResponseUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(APIPath.CART_API)
@RequiredArgsConstructor
public class CartController {
    private final CartService cartService;

    @PreAuthorize("hasRole('ADMIN') or hasRole('USER')")
    @PostMapping()
    public ResponseEntity<?> addProductToCartHandler(
            @RequestBody CartRequest cartRequest
    ) {
        cartService.addProductToCart(cartRequest);
        return ResponseUtil.buildResponse(
                HttpStatus.OK,
                CustomerResponseMessage.CUSTOMER_ADD_PRODUCT_SUCCESS,
                null
        );
    }

    @PreAuthorize("hasRole('ADMIN') or hasRole('USER')")
    @GetMapping()
    public ResponseEntity<?> getAllProductFromCartHandler() {
        return ResponseUtil.buildResponse(
                HttpStatus.OK,
                CustomerResponseMessage.CUSTOMER_GET_PRODUCTS_SUCCESS,
                cartService.getAllProduct()
        );
    }

    @PreAuthorize("hasRole('ADMIN') or hasRole('USER')")
    @PutMapping("/{cartId}")
    public ResponseEntity<?> updateCartHandler(
            @PathVariable String cartId,
            @RequestBody CartUpdateProductQuantityRequest request
    ) {
        cartService.updateProductQuantity(cartId, request);
        return ResponseUtil.buildResponse(
                HttpStatus.OK,
                CustomerResponseMessage.CUSTOMER_UPDATE_PRODUCTS_SUCCESS,
                null
        );
    }

    @PreAuthorize("hasRole('ADMIN') or hasRole('USER')")
    @DeleteMapping("/{cartId}")
    public ResponseEntity<?> deleteCartHandler(
            @PathVariable String cartId
    ) {
        cartService.deleteCartById(cartId);
        return ResponseUtil.buildResponse(
                HttpStatus.OK,
                CustomerResponseMessage.CUSTOMER_DELETE_PRODUCTS_SUCCESS,
                null
        );
    }
}
