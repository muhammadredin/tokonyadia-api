package io.github.muhammadredin.tokonyadiaapi.service;

import io.github.muhammadredin.tokonyadiaapi.dto.response.FileInfo;
import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Path;


public interface FileStorageService {
    FileInfo storeImage(MultipartFile image, Path path);

    Resource downloadFile(String path);

    void deleteFile(String path);
}
