package io.github.muhammadredin.tokonyadiaapi.service;


import io.github.muhammadredin.tokonyadiaapi.dto.request.PagingAndSortingRequest;
import io.github.muhammadredin.tokonyadiaapi.dto.request.SearchStoreRequest;
import io.github.muhammadredin.tokonyadiaapi.dto.request.StoreRequest;
import io.github.muhammadredin.tokonyadiaapi.dto.response.StoreOrderResponse;
import io.github.muhammadredin.tokonyadiaapi.dto.response.StoreResponse;
import io.github.muhammadredin.tokonyadiaapi.dto.response.StoreWithProductsResponse;
import io.github.muhammadredin.tokonyadiaapi.entity.Store;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface StoreService {
    StoreResponse createStore(StoreRequest store);

    StoreResponse getStoreById(String id);

    StoreWithProductsResponse getStoreByIdWithProducts(String id);

    Store getOne(String id);

    Page<StoreResponse> getAllStore(SearchStoreRequest request);

    StoreResponse updateStore(String id, StoreRequest store);

    void deleteStore(String id);

    List<StoreOrderResponse> getAllStoreOrders(String storeId);

    @Transactional(rollbackFor = Exception.class)
    void processOrder(String orderId);
}
