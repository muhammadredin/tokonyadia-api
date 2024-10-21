package io.github.muhammadredin.tokonyadiaapi.service.impl;

import io.github.muhammadredin.tokonyadiaapi.dto.response.FileInfo;
import io.github.muhammadredin.tokonyadiaapi.service.FileStorageService;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
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
@Slf4j
public class FileStorageServiceImpl implements FileStorageService {
    private final Integer MAX_SIZE;
    private final Path ROOT_PATH;
    private final List<String> allowedImageTypes;

    public FileStorageServiceImpl(
            @Value("${tokonyadia.file-size}") Integer maxSize,
            @Value("${tokonyadia.root-file-path}") String rootPath,
            @Value("${tokonyadia.allowed-image-types}") List<String> allowedImageTypes
    ) {
        this.MAX_SIZE = maxSize;
        this.ROOT_PATH = Paths.get(rootPath).normalize();
        this.allowedImageTypes = allowedImageTypes;
    }

    @PostConstruct
    public void initDirectory() {
        if (!Files.exists(ROOT_PATH)) {
            try {
                Files.createDirectories(ROOT_PATH);
                Files.setPosixFilePermissions(ROOT_PATH, PosixFilePermissions.fromString("rwxr-xr-x"));
            } catch (IOException e) {
                log.error("Error initializing directory", e);
                throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error initializing directory");
            }
        }
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public FileInfo storeImage(MultipartFile image, Path path) {
        try {
            validateImage(image);

            String fileName = System.currentTimeMillis() + "_" + image.getOriginalFilename();
            Path imagePath = ROOT_PATH.resolve(path.normalize());
            Path filePath = imagePath.resolve(fileName);

            log.info("Saving image at: {}", filePath);
            Files.copy(image.getInputStream(), filePath);
            Files.setPosixFilePermissions(filePath, PosixFilePermissions.fromString("rw-r--r--"));

            return FileInfo.builder()
                    .fileName(fileName)
                    .filePath(path + "/" + fileName)
                    .build();
        } catch (IOException e) {
            log.error("Error while saving image", e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

    private void validateImage(MultipartFile image) {
        if (image == null || image.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "File is empty or null");
        }
        if (image.getSize() > MAX_SIZE) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "File size exceeds the allowed limit");
        }
        if (!allowedImageTypes.contains(image.getContentType())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Unsupported file type");
        }
    }

    @Transactional(readOnly = true)
    @Override
    public Resource downloadFile(String path) {
        try {
            Path filePath = ROOT_PATH.resolve(path.substring(1)).normalize();
            log.info("Loading file at: {}", filePath);
            if (!Files.exists(filePath)) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "File not found");
            }
            return new UrlResource(filePath.toUri());
        } catch (IOException e) {
            log.error("Error loading file", e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void deleteFile(String path) {
        try {
            Path filePath = ROOT_PATH.resolve(path.substring(1)).normalize();
            if (!Files.exists(filePath)) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "File not found");
            }
            Files.deleteIfExists(filePath);
        } catch (IOException e) {
            log.error("Error deleting file", e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }
}
