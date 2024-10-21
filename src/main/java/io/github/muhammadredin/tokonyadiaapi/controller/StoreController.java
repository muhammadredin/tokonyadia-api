package io.github.muhammadredin.tokonyadiaapi.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.muhammadredin.tokonyadiaapi.constant.APIPath;
import io.github.muhammadredin.tokonyadiaapi.constant.CustomerResponseMessage;
import io.github.muhammadredin.tokonyadiaapi.constant.ProductResponseMessage;
import io.github.muhammadredin.tokonyadiaapi.constant.StoreResponseMessage;
import io.github.muhammadredin.tokonyadiaapi.dto.request.SearchStoreRequest;
import io.github.muhammadredin.tokonyadiaapi.dto.request.StoreRequest;
import io.github.muhammadredin.tokonyadiaapi.service.OrderService;
import io.github.muhammadredin.tokonyadiaapi.service.StoreService;
import io.github.muhammadredin.tokonyadiaapi.util.ResponseUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.util.List;
import java.util.Objects;

@RestController
@RequestMapping(APIPath.STORE_API)
@RequiredArgsConstructor
public class StoreController {
    private final StoreService storeService;
    private final OrderService orderService;
    private final ObjectMapper objectMapper;

    @GetMapping("/search")
    public ResponseEntity<?> searchStoresHandler(
            @RequestParam(required = false) String q,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String sort
    ) {
        SearchStoreRequest request = SearchStoreRequest.builder()
                .query(q)
                .page(page)
                .size(size)
                .sort(sort)
                .build();
        return ResponseUtil.buildResponsePaging(
                HttpStatus.OK,
                StoreResponseMessage.STORE_GET_SUCCESS,
                storeService.getAllStore(request)
        );
    }

    @PreAuthorize("hasRole('ADMIN') or hasRole('USER') and @permissionEvaluationServiceImpl.storeServiceEval(#id)")
    @GetMapping("/{id}")
    public ResponseEntity<?> getStoresById(
            @PathVariable String id
    ) {
        return ResponseUtil.buildResponse(
                HttpStatus.OK,
                StoreResponseMessage.STORE_GET_SUCCESS,
                storeService.getStoreById(id)
        );
    }

    @PreAuthorize("hasRole('ADMIN') or hasRole('USER')")
    @PostMapping
    public ResponseEntity<?> createStore(
            @RequestParam String store,
            @RequestParam List<MultipartFile> image
    ) {
        if (image.size() > 1) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Can't send more than one image");
        try {
            StoreRequest storeRequest = objectMapper.readValue(store, StoreRequest.class);
            return ResponseUtil.buildResponse(
                    HttpStatus.CREATED,
                    StoreResponseMessage.STORE_CREATE_SUCCESS,
                    storeService.createStore(storeRequest, image.get(0))
            );
        } catch (IOException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }

    }

    @PreAuthorize("hasRole('ADMIN') or hasRole('USER') and @permissionEvaluationServiceImpl.storeServiceEval(#id)")
    @PutMapping("/{id}")
    public ResponseEntity<?> updateStore(
            @PathVariable String id,
            @Valid @RequestBody StoreRequest store
    ) {
        return ResponseUtil.buildResponse(
                HttpStatus.OK,
                StoreResponseMessage.STORE_UPDATE_SUCCESS,
                storeService.updateStore(id, store)
        );
    }

    @PreAuthorize("hasRole('ADMIN') or hasRole('USER') and @permissionEvaluationServiceImpl.storeServiceEval(#id)")
    @PutMapping("/{id}/image")
    public ResponseEntity<?> updateStoreImageHandler(
            @PathVariable String id,
            @RequestParam List<MultipartFile> image
    ) {
        if (Objects.requireNonNull(image.get(0).getOriginalFilename()).isEmpty()) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Image should not be empty");
        if (image.size() > 1) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Can't send more than one image");
        return ResponseUtil.buildResponse(
                HttpStatus.OK,
                CustomerResponseMessage.CUSTOMER_UPDATE_SUCCESS,
                storeService.updateStoreImage(image.get(0))
        );
    }

    @PreAuthorize("hasRole('ADMIN') or hasRole('USER') and @permissionEvaluationServiceImpl.storeServiceEval(#id)")
    @DeleteMapping("/{id}/image")
    public ResponseEntity<?> deleteCustomerImageHandler(
            @PathVariable String id
    ) {
        return ResponseUtil.buildResponse(
                HttpStatus.OK,
                CustomerResponseMessage.CUSTOMER_UPDATE_SUCCESS,
                storeService.deleteStoreImage()
        );
    }

    @PreAuthorize("hasRole('ADMIN') or hasRole('USER') and @permissionEvaluationServiceImpl.storeServiceEval(#id)")
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteStore(
            @PathVariable String id
    ) {
        storeService.deleteStore(id);
        return ResponseUtil.buildResponse(
                HttpStatus.OK,
                StoreResponseMessage.STORE_DELETE_SUCCESS,
                null
        );
    }

    @PreAuthorize("hasRole('ADMIN') or hasRole('USER') and @permissionEvaluationServiceImpl.storeServiceEval(#id)")
    @GetMapping("/{id}/order")
    public ResponseEntity<?> getAllOrder(
            @PathVariable String id
    ) {
        return ResponseUtil.buildResponse(
                HttpStatus.OK,
                ProductResponseMessage.PRODUCT_GET_SUCCESS,
                storeService.getAllStoreOrders(id)
        );
    }

    @PreAuthorize("hasRole('ADMIN') or hasRole('USER') and @permissionEvaluationServiceImpl.storeServiceEval(#id)")
    @GetMapping("/{id}/order/detail/{orderId}")
    public ResponseEntity<?> getOrderDetail(
            @PathVariable String id,
            @PathVariable String orderId
    ) {
        return ResponseUtil.buildResponse(
                HttpStatus.OK,
                ProductResponseMessage.PRODUCT_GET_SUCCESS,
                orderService.getOrderDetailByStoreId(orderId)
        );
    }

    @PreAuthorize("hasRole('ADMIN') or hasRole('USER') and @permissionEvaluationServiceImpl.storeServiceEval(#id)")
    @PutMapping("/{id}/order/detail/{orderId}/process")
    public ResponseEntity<?> processOrderById(
            @PathVariable String id,
            @PathVariable String orderId
    ) {
        storeService.processOrder(orderId);
        return ResponseUtil.buildResponse(
                HttpStatus.OK,
                StoreResponseMessage.ORDER_PROCESS_SUCCESS,
                null
        );
    }
}
