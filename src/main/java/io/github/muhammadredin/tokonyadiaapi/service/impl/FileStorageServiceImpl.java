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

    public FileStorageServiceImpl(
            @Value("${tokonyadia.file-size}") Integer maxSize,
            @Value("${tokonyadia.root-file-path}") String rootPath
    ) {
        this.MAX_SIZE = maxSize;
        this.ROOT_PATH = Paths.get(rootPath).normalize();
    }

    @PostConstruct
    public void initDirectory() {
        if (!Files.exists(ROOT_PATH)) {
            try {
                Files.createDirectories(ROOT_PATH);
                Files.setPosixFilePermissions(ROOT_PATH, PosixFilePermissions.fromString("rwxr-xr-x"));
            } catch (IOException e) {
                throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error while init directory");
            }
        }
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public FileInfo storeImage(MultipartFile image, Path path) {
        try {
            if (image == null || image.isEmpty()) throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
            if (image.getSize() > MAX_SIZE) throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
            if (image.getOriginalFilename() == null || image.getOriginalFilename().isEmpty())
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
            if (!List.of("image/jpg", "image/jpeg", "image/png", "image/webp").contains(image.getContentType()))
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST);

            String originalFilename = image.getOriginalFilename();
            if (!(originalFilename.endsWith(".jpg") ||
                    originalFilename.endsWith(".jpeg") ||
                    originalFilename.endsWith(".png") ||
                    originalFilename.endsWith(".webp"))
            ) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
            }

            String fileName = System.currentTimeMillis() + "_" + originalFilename;
            Path imagePath = ROOT_PATH.resolve(path.toString().substring(1));
            Path filePath = imagePath.resolve(fileName);

            log.info("SAVING IMAGES");
            Files.copy(image.getInputStream(), filePath);
            Files.setPosixFilePermissions(filePath, PosixFilePermissions.fromString("rw-r--r--"));
            log.info("IMAGE STORED");

            return FileInfo.builder()
                    .fileName(fileName)
                    .filePath(filePath)
                    .build();
        } catch (IOException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

    @Transactional(readOnly = true)
    @Override
    public Resource downloadFile(String path) {
        try {
            Path filePath = ROOT_PATH.resolve(path.substring(1));
            log.info(filePath.toString());
            if (!Files.exists(filePath)) throw new ResponseStatusException(HttpStatus.NOT_FOUND, "file not found");
            return new UrlResource(filePath.toUri());
        } catch (IOException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void deleteFile(String path) {
        try {
            Path filePath = ROOT_PATH.resolve(path.substring(1));
            if (!Files.exists(filePath)) throw new ResponseStatusException(HttpStatus.NOT_FOUND, "file not found");
            Files.deleteIfExists(filePath);
        } catch (IOException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }
}
