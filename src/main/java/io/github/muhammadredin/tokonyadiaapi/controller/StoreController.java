package io.github.muhammadredin.tokonyadiaapi.controller;

import io.github.muhammadredin.tokonyadiaapi.constant.APIPath;
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

@RestController
@RequestMapping(APIPath.STORE_API)
@RequiredArgsConstructor
public class StoreController {
    private final StoreService storeService;
    private final OrderService orderService;

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
            @Valid @RequestBody StoreRequest store
    ) {
        return ResponseUtil.buildResponse(
                HttpStatus.CREATED,
                StoreResponseMessage.STORE_CREATE_SUCCESS,
                storeService.createStore(store)
        );
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
    @GetMapping("/{id}/order-details")
    public ResponseEntity<?> getAllOrderDetails(
            @PathVariable String id
    ) {
        return ResponseUtil.buildResponse(
                HttpStatus.OK,
                StoreResponseMessage.STORE_GET_SUCCESS,
                orderService.getAllTransactionDetailsByStoreId(id)
        );
    }
}
