package io.github.muhammadredin.tokonyadiaapi.controller;

import io.github.muhammadredin.tokonyadiaapi.entity.Product;
import io.github.muhammadredin.tokonyadiaapi.entity.Store;
import io.github.muhammadredin.tokonyadiaapi.service.ProductService;
import io.github.muhammadredin.tokonyadiaapi.service.StoreService;
import org.springframework.beans.factory.annotation.Autowired;
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
    public List<Product> getAllProductsHandler() {
        return productService.getAllStore();
    }

    @GetMapping("/{id}")
    public Product getProductById(
            @PathVariable String id
    ) {
        return productService.getProductById(id);
    }

    @PostMapping
    public Product createProduct(
            @RequestBody Product product
    ) {
        return productService.createProduct(product);
    }

    @PutMapping("/{id}")
    public Product updateCustomer(
            @PathVariable String id,
            @RequestBody Product product
    ) {
        return productService.updateProduct(id, product);
    }

    @DeleteMapping("/{id}")
    public String deleteCustomer(
            @PathVariable String id
    ) {
        productService.deleteStore(id);
        return "Product successfully deleted";
    }
}
