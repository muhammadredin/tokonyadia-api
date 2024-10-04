package io.github.muhammadredin.tokonyadiaapi.service.impl;

import io.github.muhammadredin.tokonyadiaapi.entity.Product;
import io.github.muhammadredin.tokonyadiaapi.repository.ProductRepository;
import io.github.muhammadredin.tokonyadiaapi.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class ProductServiceImpl implements ProductService {
    private final ProductRepository productRepository;

    @Autowired
    public ProductServiceImpl(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @Override
    public Product createProduct(Product product) {
        return productRepository.save(product);
    }

    @Override
    public Product getProductById(String id) {
        Product product = productRepository.findById(id).orElse(null);
        if (product == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Customer not found");
        }
        return product;
    }

    @Override
    public List<Product> getAllStore() {
        return productRepository.findAll();
    }

    @Override
    public Product updateProduct(String id, Product product) {
        Product getProduct = productRepository.findById(id).orElse(null);
        if (getProduct == null) {
            throw new RuntimeException("Product not found");
        }
        getProduct.setName(product.getName());
        getProduct.setPrice(product.getPrice());
        getProduct.setDescription(product.getDescription());
        getProduct.setStock(product.getStock());
        getProduct.setStore(product.getStore());
        return productRepository.save(getProduct);
    }

    @Override
    public void deleteStore(String id) {
        Product product = productRepository.findById(id).orElse(null);
        if (product == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Product not found");
        }
        productRepository.delete(product);
    }
}
