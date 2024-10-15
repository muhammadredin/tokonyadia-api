package io.github.muhammadredin.tokonyadiaapi.service.impl;

import io.github.muhammadredin.tokonyadiaapi.constant.ProductResponseMessage;
import io.github.muhammadredin.tokonyadiaapi.dto.request.SearchProductRequest;
import io.github.muhammadredin.tokonyadiaapi.dto.response.ProductResponse;
import io.github.muhammadredin.tokonyadiaapi.dto.request.ProductRequest;
import io.github.muhammadredin.tokonyadiaapi.entity.Product;
import io.github.muhammadredin.tokonyadiaapi.entity.Store;
import io.github.muhammadredin.tokonyadiaapi.entity.UserAccount;
import io.github.muhammadredin.tokonyadiaapi.repository.ProductRepository;
import io.github.muhammadredin.tokonyadiaapi.service.AuthService;
import io.github.muhammadredin.tokonyadiaapi.service.ProductService;
import io.github.muhammadredin.tokonyadiaapi.service.StoreService;
import io.github.muhammadredin.tokonyadiaapi.specification.ProductSpecification;
import io.github.muhammadredin.tokonyadiaapi.util.PagingUtil;
import io.github.muhammadredin.tokonyadiaapi.util.SortUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {
    private final ProductRepository productRepository;
    private final StoreService storeService;
    private final AuthService authService;

    @Override
    public ProductResponse createProduct(ProductRequest product) {
        Store store = storeService.getOne(product.getStoreId());
        List<String> errors = checkProduct(store.getId());

        if (!errors.isEmpty()) throw new ResponseStatusException(HttpStatus.FORBIDDEN, errors.toString());

        return toProductResponse(productRepository.save(toProduct(product, store)));
    }

    @Override
    public Product getOne(String id) {
        return productRepository.findById(id).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, ProductResponseMessage.PRODUCT_NOT_FOUND)
        );
    }

    @Override
    public ProductResponse getProductById(String id) {
        Product product = getOne(id);
        return toProductResponse(product);
    }

    @Override
    public Page<ProductResponse> searchProduct(SearchProductRequest request) {
        Sort sortBy = SortUtil.getSort(request.getSort());

        Specification<Product> specification = ProductSpecification.product(request);
        Page<Product> productPage = productRepository.findAll(specification, PagingUtil.getPageable(request, sortBy));
        return productPage.map(this::toProductResponse);
    }

    @Override
    public ProductResponse updateProduct(String id, ProductRequest product) {
        Store store = storeService.getOne(product.getStoreId());

        Product getProduct = getOne(id);
        getProduct.setName(product.getName());
        getProduct.setPrice(product.getPrice());
        getProduct.setDescription(product.getDescription());
        getProduct.setStock(product.getStock());
        getProduct.setStore(store);

        return toProductResponse(productRepository.save(getProduct));
    }

    @Override
    public void deleteProduct(String id) {
        Product product = getOne(id);
        productRepository.delete(product);
    }

    private List<String> checkProduct(String storeId) {
        List<String> errors = new ArrayList<>();
        UserAccount authentication = authService.getAuthentication();

        try {
            if (!authentication.getStore().getId().equals(storeId)) {
                errors.add("User doesn't have permission to create, update, and delete product");
            }
            return errors;
        } catch (NullPointerException e) {
            errors.add("User doesn't have permission to create, update, and delete product");
            return errors;
        }
    }

    private ProductResponse toProductResponse(Product product) {
        return ProductResponse.builder()
                .productId(product.getId())
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
