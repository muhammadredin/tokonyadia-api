package io.github.muhammadredin.tokonyadiaapi.service.impl;

import io.github.muhammadredin.tokonyadiaapi.dto.response.FileDownloadResponse;
import io.github.muhammadredin.tokonyadiaapi.dto.response.FileInfo;
import io.github.muhammadredin.tokonyadiaapi.entity.Store;
import io.github.muhammadredin.tokonyadiaapi.entity.StoreImage;
import io.github.muhammadredin.tokonyadiaapi.repository.StoreImageRepository;
import io.github.muhammadredin.tokonyadiaapi.service.FileStorageService;
import io.github.muhammadredin.tokonyadiaapi.service.StoreImageService;
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

@Service
public class StoreImageServiceImpl implements StoreImageService {
    private final StoreImageRepository storeImageRepository;
    private final FileStorageService fileStorageService;
    private final Path ROOT_PATH;
    private final Path IMAGE_PATH;

    @Autowired
    public StoreImageServiceImpl(
            StoreImageRepository storeImageRepository,
            FileStorageService fileStorageService,
            @Value("${tokonyadia.root-file-path}") String ROOT_PATH,
            @Value("${tokonyadia.store-image-path}") String IMAGE_PATH
    ) {
        this.storeImageRepository = storeImageRepository;
        this.fileStorageService = fileStorageService;
        this.ROOT_PATH = Paths.get(ROOT_PATH).normalize();
        this.IMAGE_PATH = Paths.get(IMAGE_PATH).normalize();
    }

    @PostConstruct
    public void init() {
        Path path = ROOT_PATH.resolve(IMAGE_PATH.toString().substring(1));
        if (!Files.exists(path)) {
            try {
                Files.createDirectories(path);
                Files.setPosixFilePermissions(path, PosixFilePermissions.fromString("rwxr-xr-x"));
            } catch (IOException e) {
                throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
            }
        }
    }

    @Transactional(readOnly = true)
    @Override
    public FileDownloadResponse getById(String id) {
        StoreImage storeImage = getOne(id);

        Resource resource = fileStorageService.downloadFile(storeImage.getFilePath());

        return FileDownloadResponse.builder()
                .resource(resource)
                .contentType(storeImage.getContentType())
                .build();
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public StoreImage saveImage(MultipartFile request, Store store) {
        FileInfo fileInfo = fileStorageService.storeImage(request, IMAGE_PATH);

        StoreImage image = StoreImage.builder()
                .store(store)
                .size(request.getSize())
                .contentType(request.getContentType())
                .fileName(fileInfo.getFileName())
                .filePath(fileInfo.getFilePath())
                .build();

        return storeImageRepository.saveAndFlush(image);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void deleteImage(StoreImage image) {
        fileStorageService.deleteFile(image.getFilePath());
        storeImageRepository.delete(image);
        storeImageRepository.flush();
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public StoreImage updateImage(MultipartFile request, Store store) {
        if (store.getStoreImage() != null) deleteImage(store.getStoreImage());
        return saveImage(request, store);
    }

    private StoreImage getOne(String id) {
        return storeImageRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Image not found"));
    }
}
