package io.github.muhammadredin.tokonyadiaapi.service.impl;

import io.github.muhammadredin.tokonyadiaapi.constant.ProductResponseMessage;
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
        List<String> errors = checkProduct(store.getId());

        if (!errors.isEmpty()) throw new ResponseStatusException(HttpStatus.FORBIDDEN, errors.toString());

        Product savedProduct = toProduct(request, store);
        savedProduct = productRepository.saveAndFlush(savedProduct);

        if (images != null && !images.isEmpty()) {
            List<ProductImage> productImages = productImageService.saveImageBulk(images, savedProduct);
            savedProduct.setProductImages(productImages);
        }

        return toProductResponse(savedProduct);
    }

    @Transactional(readOnly = true)
    @Override
    public Product getOne(String id) {
        return productRepository.findById(id).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, ProductResponseMessage.PRODUCT_NOT_FOUND)
        );
    }

    @Transactional(readOnly = true)
    @Override
    public ProductResponse getProductById(String id) {
        Product product = getOne(id);
        return toProductResponse(product);
    }

    @Transactional(readOnly = true)
    @Override
    public Page<ProductResponse> searchProduct(SearchProductRequest request) {
        Sort sortBy = SortUtil.getSort(request.getSort());

        Specification<Product> specification = ProductSpecification.product(request);
        Page<Product> productPage = productRepository.findAll(specification, PagingUtil.getPageable(request, sortBy));
        return productPage.map(this::toProductResponse);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public ProductResponse updateProduct(String id, ProductRequest request) {
        validationUtil.validate(request);
        Product product = getOne(id);
        product.setName(request.getName());
        product.setPrice(request.getPrice());
        product.setDescription(request.getDescription());
        product.setStock(request.getStock());

        return toProductResponse(productRepository.save(product));
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public ProductResponse addProductImage(String productId, MultipartFile image) {
        Product product = getOne(productId);
        ProductImage productImage = productImageService.saveImage(image, product);
        product.getProductImages().add(productImage);
        return toProductResponse(productRepository.saveAndFlush(product));
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public ProductResponse deleteProductImage(String productId, String imageId) {
        ProductImage productImage = productImageService.getOne(imageId);
        Product product = getOne(productId);

        if (!Objects.equals(product.getId(), productImage.getProduct().getId()))
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Image not found");

        List<ProductImage> productImages = product.getProductImages();
        productImages.remove(productImage);
        product.setProductImages(productImages);

        return toProductResponse(productRepository.saveAndFlush(product));
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void deleteProduct(String id) {
        Product product = getOne(id);
        List<ProductImage> productImages = product.getProductImages();
        for (ProductImage productImage : productImages) {
            productImageService.deleteImage(productImage.getId());
        }
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
        return Product.builder()
                .name(product.getName())
                .description(product.getDescription())
                .price(product.getPrice())
                .stock(product.getStock())
                .store(store)
                .build();
    }
}
