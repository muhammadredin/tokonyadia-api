package io.github.muhammadredin.tokonyadiaapi.service;

import io.github.muhammadredin.tokonyadiaapi.dto.response.FileDownloadResponse;
import io.github.muhammadredin.tokonyadiaapi.entity.Product;
import io.github.muhammadredin.tokonyadiaapi.entity.ProductImage;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface ProductImageService {
    List<ProductImage> saveImageBulk(List<MultipartFile> images, Product product);

    FileDownloadResponse getById(String imageId);

    void deleteImage(String imageId);

    ProductImage saveImage(MultipartFile file, Product product);

    ProductImage getOne(String imageId);
}
