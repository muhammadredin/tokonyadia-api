package io.github.muhammadredin.tokonyadiaapi.util;

import io.github.muhammadredin.tokonyadiaapi.dto.response.CommonResponse;
import io.github.muhammadredin.tokonyadiaapi.dto.response.PagingResponse;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class ResponseUtil {
    public static <T> ResponseEntity<CommonResponse<T>> buildResponse(HttpStatus status, String message, T body) {
        CommonResponse<T> response = new CommonResponse<>(status.value(), message, body, null);
        return ResponseEntity.status(status)
                .body(response);
    }

    public static <T> ResponseEntity<CommonResponse<?>> buildResponsePaging(HttpStatus status, String message, Page<T> body) {
        PagingResponse pagingResponse = PagingResponse.builder()
                .totalPages(body.getTotalPages())
                .totalItems(body.getTotalElements())
                .page(body.getNumber() + 1)
                .size(body.getSize())
                .build();
        CommonResponse<?> response = new CommonResponse<>(status.value(), message, body.getContent(), pagingResponse);
        return ResponseEntity.status(status)
                .body(response);
    }
}
