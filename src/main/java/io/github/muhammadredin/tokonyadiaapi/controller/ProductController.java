package io.github.muhammadredin.tokonyadiaapi.controller;

import io.github.muhammadredin.tokonyadiaapi.constant.APIPath;
import io.github.muhammadredin.tokonyadiaapi.constant.ProductResponseMessage;
import io.github.muhammadredin.tokonyadiaapi.dto.request.ProductRequest;
import io.github.muhammadredin.tokonyadiaapi.dto.request.SearchProductRequest;
import io.github.muhammadredin.tokonyadiaapi.service.ProductService;
import io.github.muhammadredin.tokonyadiaapi.util.ResponseUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(APIPath.PRODUCT_API)
@RequiredArgsConstructor
public class ProductController {
    private final ProductService productService;

    @GetMapping("/search")
    public ResponseEntity<?> searchProductsHandler(
            @RequestParam(required = false) String q,
            @RequestParam(required = false) Integer minPrice,
            @RequestParam(required = false) Integer maxPrice,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String sort
    ) {
        SearchProductRequest request = SearchProductRequest.builder()
                .query(q)
                .minPrice(minPrice)
                .maxPrice(maxPrice)
                .page(page)
                .size(size)
                .sort(sort)
                .build();

        return ResponseUtil.buildResponsePaging(
                HttpStatus.OK,
                ProductResponseMessage.PRODUCT_GET_SUCCESS,
                productService.searchProduct(request)
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getProductByIdHandler(
            @PathVariable String id
    ) {
        return ResponseUtil.buildResponse(
                HttpStatus.OK,
                ProductResponseMessage.PRODUCT_GET_SUCCESS,
                productService.getProductById(id)
        );
    }

    @PreAuthorize("hasRole('ADMIN') or hasRole('USER')")
    @PostMapping
    public ResponseEntity<?> createProductHandler(
            @Valid @RequestBody ProductRequest product
    ) {
        return ResponseUtil.buildResponse(
                HttpStatus.CREATED,
                ProductResponseMessage.PRODUCT_CREATE_SUCCESS,
                productService.createProduct(product)
        );
    }

    @PreAuthorize("hasRole('ADMIN') or @permissionEvaluationServiceImpl.productServiceEval(#id)")
    @PutMapping("/{id}")
    public ResponseEntity<?> updateProductHandler(
            @PathVariable String id,
            @Valid @RequestBody ProductRequest product
    ) {
        return ResponseUtil.buildResponse(
                HttpStatus.OK,
                ProductResponseMessage.PRODUCT_UPDATE_SUCCESS,
                productService.updateProduct(id, product)
        );
    }

    @PreAuthorize("hasRole('ADMIN') or @permissionEvaluationServiceImpl.productServiceEval(#id)")
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteProductHandler(
            @PathVariable String id
    ) {
        productService.deleteProduct(id);
        return ResponseUtil.buildResponse(
                HttpStatus.OK,
                ProductResponseMessage.PRODUCT_DELETE_SUCCESS,
                null
        );
    }
}
