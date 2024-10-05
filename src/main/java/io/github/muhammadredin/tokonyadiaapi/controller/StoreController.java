package io.github.muhammadredin.tokonyadiaapi.controller;

import io.github.muhammadredin.tokonyadiaapi.dto.request.PagingAndSortingRequest;
import io.github.muhammadredin.tokonyadiaapi.dto.request.StoreRequest;
import io.github.muhammadredin.tokonyadiaapi.entity.Store;
import io.github.muhammadredin.tokonyadiaapi.service.StoreService;
import io.github.muhammadredin.tokonyadiaapi.util.ResponseUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/stores")
public class StoreController {
    private final StoreService storeService;

    @Autowired
    public StoreController(StoreService storeService) {
        this.storeService = storeService;
    }

    @GetMapping
    public ResponseEntity<?> getAllCustomersHandler(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String sort
    ) {
        PagingAndSortingRequest request = PagingAndSortingRequest.builder()
                .page(page)
                .size(size)
                .sort(sort)
                .build();
        return ResponseUtil.buildResponsePaging(
                HttpStatus.OK,
                "Stores successfully fetched from database",
                storeService.getAllStore(request)
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getCustomerById(
            @PathVariable String id
    ) {
        return ResponseUtil.buildResponse(
                HttpStatus.OK,
                "Store successfully fetched from database",
                storeService.getStoreById(id)
        );
    }

    @PostMapping
    public ResponseEntity<?> createCustomer(
            @RequestBody StoreRequest store
    ) {
        return ResponseUtil.buildResponse(
                HttpStatus.CREATED,
                "Store successfully created",
                storeService.createStore(store)
        );
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateCustomer(
            @PathVariable String id,
            @RequestBody StoreRequest store
    ) {
        return ResponseUtil.buildResponse(
                HttpStatus.OK,
                "Store successfully updated",
                storeService.updateStore(id, store)
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteCustomer(
            @PathVariable String id
    ) {
        storeService.deleteStore(id);
        return ResponseUtil.buildResponse(
                HttpStatus.OK,
                "Store successfully deleted",
                null
        );
    }
}
