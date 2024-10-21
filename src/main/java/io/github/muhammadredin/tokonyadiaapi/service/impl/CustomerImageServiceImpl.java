package io.github.muhammadredin.tokonyadiaapi.service.impl;

import io.github.muhammadredin.tokonyadiaapi.dto.response.FileDownloadResponse;
import io.github.muhammadredin.tokonyadiaapi.dto.response.FileInfo;
import io.github.muhammadredin.tokonyadiaapi.entity.Customer;
import io.github.muhammadredin.tokonyadiaapi.entity.CustomerImage;
import io.github.muhammadredin.tokonyadiaapi.entity.UserAccount;
import io.github.muhammadredin.tokonyadiaapi.repository.CustomerImageRepository;
import io.github.muhammadredin.tokonyadiaapi.service.CustomerImageService;
import io.github.muhammadredin.tokonyadiaapi.service.FileStorageService;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.PosixFilePermissions;

@Slf4j
@Service
public class CustomerImageServiceImpl implements CustomerImageService {
    private final CustomerImageRepository customerImageRepository;
    private final FileStorageService fileStorageService;
    private final Path ROOT_PATH;
    private final Path IMAGE_PATH;

    @Autowired
    public CustomerImageServiceImpl(
            @Value("${tokonyadia.root-file-path}") String ROOT_PATH,
            @Value("${tokonyadia.customer-image-path}") String IMAGE_PATH,
            CustomerImageRepository customerImageRepository,
            FileStorageService fileStorageService
    ) {
        this.customerImageRepository = customerImageRepository;
        this.ROOT_PATH = Paths.get(ROOT_PATH).normalize();
        this.IMAGE_PATH = Paths.get(IMAGE_PATH).normalize();
        this.fileStorageService = fileStorageService;
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

    @Transactional(readOnly = true)
    @Override
    public FileDownloadResponse getById(String id) {
        CustomerImage customerImage = getOne(id);

        Resource resource = fileStorageService.downloadFile(customerImage.getFilePath());

        return FileDownloadResponse.builder()
                .resource(resource)
                .contentType(customerImage.getContentType())
                .build();
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public CustomerImage saveImage(MultipartFile image, Customer customer) {
        FileInfo fileInfo = fileStorageService.storeImage(image, IMAGE_PATH);

        CustomerImage customerImage = CustomerImage.builder()
                .size(image.getSize())
                .contentType(image.getContentType())
                .fileName(fileInfo.getFileName())
                .filePath(fileInfo.getFilePath())
                .customer(customer)
                .build();

        customerImageRepository.saveAndFlush(customerImage);
        return customerImage;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void deleteImage(CustomerImage customerImage) {
        log.info("Deleting image {}", customerImage.getFilePath());
        customerImageRepository.delete(customerImage);
        customerImageRepository.flush();
        fileStorageService.deleteFile(customerImage.getFilePath());
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public CustomerImage updateImage(MultipartFile image, Customer customer) {
        if (customer.getCustomerImage() != null) deleteImage(customer.getCustomerImage());
        return saveImage(image, customer);
    }

    private CustomerImage getOne(String id) {
        return customerImageRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Image not found"));
    }
}
