package io.github.muhammadredin.tokonyadiaapi.controller;

import io.github.muhammadredin.tokonyadiaapi.dto.response.FileDownloadResponse;
import io.github.muhammadredin.tokonyadiaapi.service.CustomerImageService;
import io.github.muhammadredin.tokonyadiaapi.service.ProductImageService;
import io.github.muhammadredin.tokonyadiaapi.util.ResponseUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class FileController {
    private final ProductImageService productImageService;
    private final CustomerImageService customerImageService;

    @GetMapping("/images/products/{id}")
    public ResponseEntity<?> getProductImage(@PathVariable String id) {
        FileDownloadResponse resource = productImageService.getById(id);
        String headerValue = String.format("inline; filename=%s", resource.getResource().getFilename());
        return ResponseEntity
                .status(HttpStatus.OK)
                .header(HttpHeaders.CONTENT_DISPOSITION, headerValue)
                .contentType(MediaType.valueOf(resource.getContentType()))
                .body(resource.getResource());
    }

    @DeleteMapping("/images/products/{id}")
    public ResponseEntity<?> deleteProductImage(@PathVariable String id) {
        productImageService.deleteImage(id);
        return ResponseUtil.buildResponse(HttpStatus.OK, "Image successfully deleted", null);
    }

    @GetMapping("/images/customers/{id}")
    public ResponseEntity<?> getCustomerImage(@PathVariable String id) {
        FileDownloadResponse resource = customerImageService.getById(id);
        String headerValue = String.format("inline; filename=%s", resource.getResource().getFilename());
        return ResponseEntity
                .status(HttpStatus.OK)
                .header(HttpHeaders.CONTENT_DISPOSITION, headerValue)
                .contentType(MediaType.valueOf(resource.getContentType()))
                .body(resource.getResource());
    }
}
