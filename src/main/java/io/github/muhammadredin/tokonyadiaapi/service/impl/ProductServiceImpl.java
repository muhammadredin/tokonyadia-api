package io.github.muhammadredin.tokonyadiaapi.service.impl;

import io.github.muhammadredin.tokonyadiaapi.dto.request.PagingAndSortingRequest;
import io.github.muhammadredin.tokonyadiaapi.dto.response.ProductResponse;
import io.github.muhammadredin.tokonyadiaapi.dto.request.ProductRequest;
import io.github.muhammadredin.tokonyadiaapi.entity.Product;
import io.github.muhammadredin.tokonyadiaapi.entity.Store;
import io.github.muhammadredin.tokonyadiaapi.repository.ProductRepository;
import io.github.muhammadredin.tokonyadiaapi.service.ProductService;
import io.github.muhammadredin.tokonyadiaapi.service.StoreService;
import io.github.muhammadredin.tokonyadiaapi.util.PagingUtil;
import io.github.muhammadredin.tokonyadiaapi.util.SortUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class ProductServiceImpl implements ProductService {
    private final ProductRepository productRepository;
    private final StoreService storeService;

    @Autowired
    public ProductServiceImpl(ProductRepository productRepository, StoreService storeService) {
        this.productRepository = productRepository;
        this.storeService = storeService;
    }

    @Override
    public ProductResponse createProduct(ProductRequest product) {
        Store store = storeService.getStore(product.getStoreId());

        return toProductResponse(productRepository.save(toProduct(product, store)));
    }

    @Override
    public ProductResponse getProductById(String id) {
        Product product = productRepository.findById(id).orElse(null);
        if (product == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Product not found");
        }
        return toProductResponse(product);
    }

    @Override
    public Page<ProductResponse> getAllProduct(PagingAndSortingRequest request) {
        Sort sortBy = SortUtil.getSort(request.getSort());

        Page<Product> productPage = productRepository.findAll(PagingUtil.getPageable(request, sortBy));
        return productPage.map(this::toProductResponse);
    }

    @Override
    public ProductResponse updateProduct(String id, ProductRequest product) {
        Store store = storeService.getStore(product.getStoreId());

        Product getProduct = productRepository.findById(id).orElse(null);
        if (getProduct == null) {
            throw new RuntimeException("Product not found");
        }

        getProduct.setName(product.getName());
        getProduct.setPrice(product.getPrice());
        getProduct.setDescription(product.getDescription());
        getProduct.setStock(product.getStock());
        getProduct.setStore(store);

        return toProductResponse(productRepository.save(getProduct));
    }

    @Override
    public void deleteProduct(String id) {
        Product product = productRepository.findById(id).orElse(null);
        if (product == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Product not found");
        }
        productRepository.delete(product);
    }

    private ProductResponse toProductResponse(Product product) {
        return ProductResponse.builder()
                .id(product.getId())
                .name(product.getName())
                .description(product.getDescription())
                .price(product.getPrice())
                .stock(product.getStock())
                .storeName(product.getStore().getName())
                .build();
    }

    private Product toProduct(ProductRequest product, Store store) {
        return Product.builder()
                .name(product.getName())
                .description(product.getDescription())
                .price(product.getPrice())
                .stock(product.getStock())
                .store(store)
                .build();
    }
}
