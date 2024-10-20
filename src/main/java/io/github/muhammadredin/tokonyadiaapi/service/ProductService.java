package io.github.muhammadredin.tokonyadiaapi.service;

import io.github.muhammadredin.tokonyadiaapi.dto.request.PagingAndSortingRequest;
import io.github.muhammadredin.tokonyadiaapi.dto.request.ProductRequest;
import io.github.muhammadredin.tokonyadiaapi.dto.request.SearchProductRequest;
import io.github.muhammadredin.tokonyadiaapi.dto.response.ProductResponse;
import io.github.muhammadredin.tokonyadiaapi.entity.Product;
import io.github.muhammadredin.tokonyadiaapi.entity.Store;
import org.springframework.data.domain.Page;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface ProductService {
    ProductResponse createProduct(ProductRequest request, Store store, List<MultipartFile> images);

    Product getOne(String id);

    ProductResponse getProductById(String id);

    Page<ProductResponse> searchProduct(SearchProductRequest request);

    ProductResponse updateProduct(String id, ProductRequest product);

    ProductResponse addProductImage(String productId, MultipartFile image);

    ProductResponse deleteProductImage(String productId, String imageId);

    void deleteProduct(String id);
}
