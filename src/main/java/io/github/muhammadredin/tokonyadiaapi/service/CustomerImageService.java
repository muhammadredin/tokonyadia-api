package io.github.muhammadredin.tokonyadiaapi.service;

import io.github.muhammadredin.tokonyadiaapi.dto.response.FileDownloadResponse;
import io.github.muhammadredin.tokonyadiaapi.entity.Customer;
import io.github.muhammadredin.tokonyadiaapi.entity.CustomerImage;
import org.springframework.web.multipart.MultipartFile;

public interface CustomerImageService {
    FileDownloadResponse getById(String id);

    CustomerImage saveImage(MultipartFile image, Customer customer);

    void deleteImage(CustomerImage customerImage);

    CustomerImage updateImage(MultipartFile image, Customer customer);
}
