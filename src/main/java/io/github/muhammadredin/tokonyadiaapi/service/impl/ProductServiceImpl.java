package io.github.muhammadredin.tokonyadiaapi.service.impl;

import io.github.muhammadredin.tokonyadiaapi.constant.ProductResponseMessage;
import io.github.muhammadredin.tokonyadiaapi.dto.request.ProductUpdateRequest;
import io.github.muhammadredin.tokonyadiaapi.dto.request.SearchProductRequest;
import io.github.muhammadredin.tokonyadiaapi.dto.response.FileResponse;
import io.github.muhammadredin.tokonyadiaapi.dto.response.ProductResponse;
import io.github.muhammadredin.tokonyadiaapi.dto.request.ProductRequest;
import io.github.muhammadredin.tokonyadiaapi.entity.Product;
import io.github.muhammadredin.tokonyadiaapi.entity.ProductImage;
import io.github.muhammadredin.tokonyadiaapi.entity.Store;
import io.github.muhammadredin.tokonyadiaapi.entity.UserAccount;
import io.github.muhammadredin.tokonyadiaapi.repository.ProductRepository;
import io.github.muhammadredin.tokonyadiaapi.service.AuthService;
import io.github.muhammadredin.tokonyadiaapi.service.ProductImageService;
import io.github.muhammadredin.tokonyadiaapi.service.ProductService;
import io.github.muhammadredin.tokonyadiaapi.specification.ProductSpecification;
import io.github.muhammadredin.tokonyadiaapi.util.PagingUtil;
import io.github.muhammadredin.tokonyadiaapi.util.SortUtil;
import io.github.muhammadredin.tokonyadiaapi.util.ValidationUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

@Service
@Slf4j
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {
    private final ProductRepository productRepository;
    private final ProductImageService productImageService;
    private final ValidationUtil validationUtil;
    private final AuthService authService;

    @Transactional(rollbackFor = Exception.class)
    @Override
    public ProductResponse createProduct(ProductRequest request, Store store, List<MultipartFile> images) {
        log.info("Creating product for store: {}", store.getId());

        List<String> errors = checkProduct(store.getId());

        if (!errors.isEmpty()) {
            log.error("Errors occurred while checking product permissions: {}", errors);
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, errors.toString());
        }

        Product savedProduct = toProduct(request, store);
        savedProduct = productRepository.saveAndFlush(savedProduct);
        log.info("Product created with ID: {}", savedProduct.getId());

        if (images != null && !images.isEmpty()) {
            List<ProductImage> productImages = productImageService.saveImageBulk(images, savedProduct);
            savedProduct.setProductImages(productImages);
            log.info("Images saved for product ID: {}", savedProduct.getId());
        }

        return toProductResponse(savedProduct);
    }

    @Transactional(readOnly = true)
    @Override
    public Product getOne(String id) {
        log.info("Fetching product by ID: {}", id);
        return productRepository.findById(id).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, ProductResponseMessage.PRODUCT_NOT_FOUND)
        );
    }

    @Transactional(readOnly = true)
    @Override
    public ProductResponse getProductById(String id) {
        log.info("Getting product response for ID: {}", id);
        Product product = getOne(id);
        return toProductResponse(product);
    }

    @Transactional(readOnly = true)
    @Override
    public Page<ProductResponse> searchProduct(SearchProductRequest request) {
        log.info("Searching products with request: {}", request);
        Sort sortBy = SortUtil.getSort(request.getSort());
        Specification<Product> specification = ProductSpecification.product(request);
        Page<Product> productPage = productRepository.findAll(specification, PagingUtil.getPageable(request, sortBy));
        return productPage.map(this::toProductResponse);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public ProductResponse updateProduct(String id, ProductUpdateRequest request) {
        log.info("Updating product ID: {}", id);
        validationUtil.validate(request);
        Product product = getOne(id);

        UserAccount userAccount = (UserAccount) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if (!userAccount.getStore().getId().equals(product.getStore().getId()))
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);

        product.setName(request.getName());
        product.setPrice(request.getPrice());
        product.setDescription(request.getDescription());
        product.setStock(request.getStock());

        Product updatedProduct = productRepository.save(product);
        log.info("Product updated: {}", updatedProduct.getId());
        return toProductResponse(updatedProduct);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public ProductResponse addProductImage(String productId, List<MultipartFile> images) {
        log.info("Adding images to product ID: {}", productId);
        Product product = getOne(productId);
        List<ProductImage> productImages = productImageService.saveImageBulk(images, product);

        product.getProductImages().addAll(productImages);
        Product updatedProduct = productRepository.saveAndFlush(product);
        log.info("Images added to product ID: {}", updatedProduct.getId());

        return toProductResponse(updatedProduct);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public ProductResponse deleteProductImage(String productId, String imageId) {
        log.info("Deleting image ID: {} from product ID: {}", imageId, productId);
        ProductImage productImage = productImageService.getOne(imageId);
        Product product = getOne(productId);

        if (!Objects.equals(product.getId(), productImage.getProduct().getId())) {
            log.error("Image not found for product ID: {} and image ID: {}", productId, imageId);
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Image not found");
        }

        product.getProductImages().remove(productImage);
        Product updatedProduct = productRepository.saveAndFlush(product);
        log.info("Image ID: {} deleted from product ID: {}", imageId, updatedProduct.getId());

        return toProductResponse(updatedProduct);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void deleteProduct(String id) {
        log.info("Deleting product ID: {}", id);
        Product product = getOne(id);
        List<ProductImage> productImages = product.getProductImages();

        for (ProductImage productImage : productImages) {
            ProductImage image = productImageService.getOne(productImage.getId());
            productImageService.deleteImage(image.getId());
            log.info("Deleted image ID: {}", image.getId());
        }

        productRepository.delete(product);
        log.info("Product ID: {} deleted successfully", id);
    }

    private List<String> checkProduct(String storeId) {
        log.info("Checking product permissions for store ID: {}", storeId);
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
        log.debug("Converting product to response for ID: {}", product.getId());
        List<FileResponse> images = product.getProductImages() != null ?
                product.getProductImages().stream()
                        .map(image -> FileResponse.builder()
                                .id(image.getId())
                                .url("/api/images/products/" + image.getId())
                                .build()
                        ).toList() :
                Collections.emptyList();

        return ProductResponse.builder()
                .productId(product.getId())
                .name(product.getName())
                .description(product.getDescription())
                .price(product.getPrice())
                .stock(product.getStock())
                .storeName(product.getStore().getName())
                .images(images)
                .build();
    }

    private Product toProduct(ProductRequest product, Store store) {
        log.debug("Converting ProductRequest to Product for store ID: {}", store.getId());
        return Product.builder()
                .name(product.getName())
                .description(product.getDescription())
                .price(product.getPrice())
                .stock(product.getStock())
                .store(store)
                .build();
    }
}
