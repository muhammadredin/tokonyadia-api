package io.github.muhammadredin.tokonyadiaapi.service.impl;

import io.github.muhammadredin.tokonyadiaapi.dto.response.FileDownloadResponse;
import io.github.muhammadredin.tokonyadiaapi.dto.response.FileInfo;
import io.github.muhammadredin.tokonyadiaapi.entity.Product;
import io.github.muhammadredin.tokonyadiaapi.entity.ProductImage;
import io.github.muhammadredin.tokonyadiaapi.repository.ProductImageRepository;
import io.github.muhammadredin.tokonyadiaapi.service.FileStorageService;
import io.github.muhammadredin.tokonyadiaapi.service.ProductImageService;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.PosixFilePermissions;
import java.util.List;

@Service
public class ProductImageServiceImpl implements ProductImageService {
    private final ProductImageRepository productImageRepository;
    private final FileStorageService fileStorageService;
    private final Path ROOT_PATH;
    private final Path IMAGE_PATH;

    @Autowired
    public ProductImageServiceImpl(
            @Value("${tokonyadia.root-file-path}") String ROOT_PATH,
            @Value("${tokonyadia.product-image-path}") String IMAGE_PATH,
            ProductImageRepository productImageRepository,
            FileStorageService fileStorageService
    ) {
        this.productImageRepository = productImageRepository;
        this.fileStorageService = fileStorageService;
        this.ROOT_PATH = Paths.get(ROOT_PATH);
        this.IMAGE_PATH = Paths.get(IMAGE_PATH);
    }

    @PostConstruct
    public void initDirectory() {
        Path path = ROOT_PATH.normalize().resolve(IMAGE_PATH.toString().substring(1));

        if (!Files.exists(path)) {
            try {
                Files.createDirectories(path);
                Files.setPosixFilePermissions(path, PosixFilePermissions.fromString("rwxr-xr-x"));
            } catch (IOException e) {
                throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error while init directory");
            }
        }
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public List<ProductImage> saveImageBulk(List<MultipartFile> images, Product product) {
        if (images.size() > 5 || product.getProductImages() != null && (product.getProductImages().size() + images.size() > 5))
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Maximum product image is 5");
        return images.stream().map(image -> saveImage(image, product)).toList();
    }

    @Transactional(readOnly = true)
    @Override
    public FileDownloadResponse getById(String imageId) {
        ProductImage productImage = getOne(imageId);

        Resource urlResource = fileStorageService.downloadFile(productImage.getFilePath());

        return FileDownloadResponse.builder()
                .resource(urlResource)
                .contentType(productImage.getContentType())
                .build();
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void deleteImage(String imageId) {
        ProductImage productImage = getOne(imageId);
        productImageRepository.delete(productImage);
        productImageRepository.flush();
        fileStorageService.deleteFile(productImage.getFilePath());
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public ProductImage saveImage(MultipartFile file, Product product) {
        FileInfo fileInfo = fileStorageService.storeImage(file, IMAGE_PATH);

        ProductImage productImage = ProductImage.builder()
                .fileName(fileInfo.getFileName())
                .contentType(file.getContentType())
                .size(file.getSize())
                .filePath(fileInfo.getFilePath())
                .product(product)
                .build();

        productImageRepository.saveAndFlush(productImage);
        return productImage;
    }

    @Transactional(readOnly = true)
    @Override
    public ProductImage getOne(String imageId) {
        return productImageRepository.findById(imageId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Image not found"));
    }
}
