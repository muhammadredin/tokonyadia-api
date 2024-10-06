package io.github.muhammadredin.tokonyadiaapi.controller;

import io.github.muhammadredin.tokonyadiaapi.constant.StoreResponseMessage;
import io.github.muhammadredin.tokonyadiaapi.dto.request.PagingAndSortingRequest;
import io.github.muhammadredin.tokonyadiaapi.dto.request.SearchStoreRequest;
import io.github.muhammadredin.tokonyadiaapi.dto.request.StoreRequest;
import io.github.muhammadredin.tokonyadiaapi.service.StoreService;
import io.github.muhammadredin.tokonyadiaapi.util.ResponseUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/stores")
public class StoreController {
    private final StoreService storeService;

    @Autowired
    public StoreController(StoreService storeService) {
        this.storeService = storeService;
    }

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

    @PostMapping
    public ResponseEntity<?> createStore(
            @RequestBody StoreRequest store
    ) {
        return ResponseUtil.buildResponse(
                HttpStatus.CREATED,
                StoreResponseMessage.STORE_CREATE_SUCCESS,
                storeService.createStore(store)
        );
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateStore(
            @PathVariable String id,
            @RequestBody StoreRequest store
    ) {
        return ResponseUtil.buildResponse(
                HttpStatus.OK,
                StoreResponseMessage.STORE_UPDATE_SUCCESS,
                storeService.updateStore(id, store)
        );
    }

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
}
