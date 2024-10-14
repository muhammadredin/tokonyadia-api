package io.github.muhammadredin.tokonyadiaapi.controller;

import io.github.muhammadredin.tokonyadiaapi.constant.APIPath;
import io.github.muhammadredin.tokonyadiaapi.constant.CustomerResponseMessage;
import io.github.muhammadredin.tokonyadiaapi.dto.request.*;
import io.github.muhammadredin.tokonyadiaapi.service.CartService;
import io.github.muhammadredin.tokonyadiaapi.service.CustomerService;
import io.github.muhammadredin.tokonyadiaapi.util.ResponseUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(APIPath.CUSTOMER_API)
@RequiredArgsConstructor
public class CustomerController {
    private final CustomerService customerService;
    private final CartService cartService;

    @PreAuthorize("hasRole('ADMIN') or hasRole('STORE')")
    @GetMapping("/search")
    public ResponseEntity<?> searchCustomersHandler(
            @RequestParam(required = false) String q,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String sort
    ) {
        SearchCustomerRequest request = SearchCustomerRequest.builder()
                .query(q)
                .page(page)
                .size(size)
                .build();
        return ResponseUtil.buildResponsePaging(
                HttpStatus.OK,
                CustomerResponseMessage.CUSTOMER_GET_SUCCESS,
                customerService.searchCustomers(request)
        );
    }

    @PreAuthorize("hasRole('ADMIN') or hasRole('USER') and @permissionEvaluationServiceImpl.customerServiceEval(#id)")
    @GetMapping("/{id}")
    public ResponseEntity<?> getCustomerByIdHandler(
            @PathVariable String id
    ) {
        return ResponseUtil.buildResponse(
                HttpStatus.OK,
                CustomerResponseMessage.CUSTOMER_GET_SUCCESS,
                customerService.getCustomerById(id));
    }

    @PreAuthorize("hasRole('ADMIN') or hasRole('USER')")
    @PostMapping
    public ResponseEntity<?> createCustomerHandler(
            @Valid @RequestBody CustomerRequest customer
    ) {
        return ResponseUtil.buildResponse(
                HttpStatus.CREATED,
                CustomerResponseMessage.CUSTOMER_CREATE_SUCCESS,
                customerService.createCustomer(customer)
        );
    }

    @PreAuthorize("hasRole('ADMIN') or hasRole('USER') and @permissionEvaluationServiceImpl.customerServiceEval(#id)")
    @PutMapping("/{id}")
    public ResponseEntity<?> updateCustomerHandler(
            @PathVariable String id,
            @Valid @RequestBody CustomerUpdateRequest customer
    ) {
        return ResponseUtil.buildResponse(
                HttpStatus.OK,
                CustomerResponseMessage.CUSTOMER_UPDATE_SUCCESS,
                customerService.updateCustomer(id, customer)
        );
    }

    @PreAuthorize("hasRole('ADMIN') or hasRole('USER') and @permissionEvaluationServiceImpl.customerServiceEval(#id)")
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteCustomerHandler(
            @PathVariable String id
    ) {
        customerService.deleteCustomer(id);
        return ResponseUtil.buildResponse(
                HttpStatus.OK,
                CustomerResponseMessage.CUSTOMER_DELETE_SUCCESS,
                null
        );
    }

    @PreAuthorize("hasRole('ADMIN') or hasRole('USER') and @permissionEvaluationServiceImpl.customerServiceEval(#id)")
    @PostMapping("/{id}/cart")
    public ResponseEntity<?> addProductToCartHandler(
            @PathVariable String id,
            @RequestBody CartRequest cartRequest
            ) {
        cartService.addProductToCart(id, cartRequest);
        return ResponseUtil.buildResponse(
                HttpStatus.OK,
                CustomerResponseMessage.CUSTOMER_ADD_PRODUCT_SUCCESS,
                null
        );
    }

    @PreAuthorize("hasRole('ADMIN') or hasRole('USER') and @permissionEvaluationServiceImpl.customerServiceEval(#id)")
    @GetMapping("/{id}/cart")
    public ResponseEntity<?> getAllProductFromCartHandler(
            @PathVariable String id
    ) {
        return ResponseUtil.buildResponse(
                HttpStatus.OK,
                CustomerResponseMessage.CUSTOMER_GET_PRODUCTS_SUCCESS,
                cartService.getAllProduct(id)
        );
    }

    @PreAuthorize("hasRole('ADMIN') or hasRole('USER') and @permissionEvaluationServiceImpl.customerServiceEval(#id)")
    @PutMapping("/{id}/cart/{cartId}")
    public ResponseEntity<?> updateCartHandler(
            @PathVariable String id,
            @PathVariable String cartId,
            @RequestBody CartUpdateProductQuantityRequest request
    ) {
        cartService.updateProductQuantity(id, cartId, request);
        return ResponseUtil.buildResponse(
                HttpStatus.OK,
                CustomerResponseMessage.CUSTOMER_UPDATE_PRODUCTS_SUCCESS,
                null
        );
    }

    @PreAuthorize("hasRole('ADMIN') or hasRole('USER') and @permissionEvaluationServiceImpl.customerServiceEval(#id)")
    @DeleteMapping("/{id}/cart/{cartId}")
    public ResponseEntity<?> deleteCartHandler(
            @PathVariable String id,
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
