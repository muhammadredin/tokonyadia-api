package io.github.muhammadredin.tokonyadiaapi.controller;

import io.github.muhammadredin.tokonyadiaapi.dto.request.PagingAndSortingRequest;
import io.github.muhammadredin.tokonyadiaapi.dto.request.ProductRequest;
import io.github.muhammadredin.tokonyadiaapi.entity.Product;
import io.github.muhammadredin.tokonyadiaapi.service.ProductService;
import io.github.muhammadredin.tokonyadiaapi.util.ResponseUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/products")
public class ProductController {
    private final ProductService productService;

    @Autowired
    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping
    public ResponseEntity<?> getAllProductsHandler(
                @RequestParam(defaultValue = "1") Integer page,
                @RequestParam(defaultValue = "10") Integer size,
                @RequestParam(required = false) String sort

    ) {
        PagingAndSortingRequest request = PagingAndSortingRequest.builder()
                .page(page)
                .size(size)
                .sort(sort)
                .build();

        return ResponseUtil.buildResponsePaging(
                HttpStatus.OK,
                "Products successfully fetched from database",
                productService.getAllProduct(request)
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getProductByIdHandler(
            @PathVariable String id
    ) {
        return ResponseUtil.buildResponse(
                HttpStatus.OK,
                "Product successfully fetched from database",
                productService.getProductById(id)
        );
    }

    @PostMapping
    public ResponseEntity<?> createProductHandler(
            @RequestBody ProductRequest product
    ) {
        return ResponseUtil.buildResponse(
                HttpStatus.CREATED,
                "Product successfully created",
                productService.createProduct(product)
        );
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateProductHandler(
            @PathVariable String id,
            @RequestBody ProductRequest product
    ) {
        return ResponseUtil.buildResponse(
                HttpStatus.OK,
                "Product successfully updated",
                productService.updateProduct(id, product)
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteProductHandler(
            @PathVariable String id
    ) {
        productService.deleteProduct(id);
        return ResponseUtil.buildResponse(
                HttpStatus.OK,
                "Product successfully deleted",
                null
        );
    }
}
