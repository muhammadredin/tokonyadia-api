package io.github.muhammadredin.tokonyadiaapi.service;


import io.github.muhammadredin.tokonyadiaapi.dto.request.PagingAndSortingRequest;
import io.github.muhammadredin.tokonyadiaapi.dto.request.SearchOrderRequest;
import io.github.muhammadredin.tokonyadiaapi.dto.request.SearchStoreRequest;
import io.github.muhammadredin.tokonyadiaapi.dto.request.StoreRequest;
import io.github.muhammadredin.tokonyadiaapi.dto.response.OrderResponse;
import io.github.muhammadredin.tokonyadiaapi.dto.response.StoreResponse;
import io.github.muhammadredin.tokonyadiaapi.dto.response.StoreWithProductsResponse;
import io.github.muhammadredin.tokonyadiaapi.entity.Store;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface StoreService {
    StoreResponse createStore(StoreRequest request, List<MultipartFile> image);

    StoreResponse getStoreById(String storeId);

    Store getOne(String storeId);

    Page<StoreResponse> getAllStore(SearchStoreRequest request);

    StoreResponse updateStore(String storeId, StoreRequest request);

    StoreResponse updateStoreImage(String storeId, List<MultipartFile> image);

    StoreResponse deleteStoreImage(String storeId);

    void deleteStore(String storeId);

    Page<OrderResponse> getAllStoreOrders(String storeId, SearchOrderRequest request);

    void processOrder(String storeId, String orderId);
}
