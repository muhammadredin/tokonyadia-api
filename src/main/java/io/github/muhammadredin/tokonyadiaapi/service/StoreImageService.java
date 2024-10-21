package io.github.muhammadredin.tokonyadiaapi.service;

import io.github.muhammadredin.tokonyadiaapi.dto.response.FileDownloadResponse;
import io.github.muhammadredin.tokonyadiaapi.entity.Store;
import io.github.muhammadredin.tokonyadiaapi.entity.StoreImage;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

public interface StoreImageService {
    @Transactional(readOnly = true)
    FileDownloadResponse getById(String id);

    @Transactional(rollbackFor = Exception.class)
    StoreImage saveImage(MultipartFile request, Store store);

    @Transactional(rollbackFor = Exception.class)
    void deleteImage(StoreImage image);

    @Transactional(rollbackFor = Exception.class)
    StoreImage updateImage(MultipartFile request, Store store);
}
