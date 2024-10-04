package io.github.muhammadredin.tokonyadiaapi.service;

import io.github.muhammadredin.tokonyadiaapi.entity.Product;

import java.util.List;

public interface ProductService {
    Product createProduct(Product product);

    Product getProductById(String id);

    List<Product> getAllStore();

    Product updateProduct(String id, Product product);

    void deleteStore(String id);
}
