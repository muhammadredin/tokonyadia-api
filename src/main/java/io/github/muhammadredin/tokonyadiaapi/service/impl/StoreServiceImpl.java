package io.github.muhammadredin.tokonyadiaapi.service.impl;

import io.github.muhammadredin.tokonyadiaapi.constant.StoreResponseMessage;
import io.github.muhammadredin.tokonyadiaapi.dto.request.SearchStoreRequest;
import io.github.muhammadredin.tokonyadiaapi.dto.request.StoreRequest;
import io.github.muhammadredin.tokonyadiaapi.dto.response.ProductResponse;
import io.github.muhammadredin.tokonyadiaapi.dto.response.StoreResponse;
import io.github.muhammadredin.tokonyadiaapi.dto.response.StoreWithProductsResponse;
import io.github.muhammadredin.tokonyadiaapi.entity.Store;
import io.github.muhammadredin.tokonyadiaapi.repository.StoreRepository;
import io.github.muhammadredin.tokonyadiaapi.service.AuthService;
import io.github.muhammadredin.tokonyadiaapi.service.StoreService;
import io.github.muhammadredin.tokonyadiaapi.specification.StoreSpecification;
import io.github.muhammadredin.tokonyadiaapi.util.PagingUtil;
import io.github.muhammadredin.tokonyadiaapi.util.SortUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class StoreServiceImpl implements StoreService {
    private final StoreRepository storeRepository;
    private final AuthService authService;

    @Override
    public StoreResponse createStore(StoreRequest store) {
        List<String> errors = checkStore(store.getNoSiup(), store.getName());

        if (!errors.isEmpty()) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, errors.toString());

        return toStoreResponse(storeRepository.save(toStore(store)));
    }

    @Override
    public StoreResponse getStoreById(String id) {
        Store store = getOne(id);
        return toStoreResponse(store);
    }

    @Override
    public StoreWithProductsResponse getStoreByIdWithProducts(String id) {
        Store store = getOne(id);
        return toStoreWithProductsResponse(store);
    }

    @Override
    public Store getOne(String id) {
        return storeRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, StoreResponseMessage.STORE_NOT_FOUND_ERROR));
    }


    @Override
    public Page<StoreResponse> getAllStore(SearchStoreRequest request) {
        Sort sortBy = SortUtil.getSort(request.getSort());

        Specification<Store> specification = StoreSpecification.store(request);
        Page<Store> stores = storeRepository.findAll(specification, PagingUtil.getPageable(request, sortBy));

        return stores.map(this::toStoreResponse);
    }

    @Override
    public StoreResponse updateStore(String id, StoreRequest request) {
        Store store = getOne(id);
        store.setNoSiup(request.getNoSiup());
        store.setName(request.getName());
        store.setAddress(request.getAddress());
        store.setPhoneNumber(request.getPhoneNumber());
        return toStoreResponse(storeRepository.save(store));
    }

    @Override
    public void deleteStore(String id) {
        storeRepository.delete(getOne(id));
    }

    private List<String> checkStore(String noSiup, String phoneNumber) {
        List<String> errors = new ArrayList<>();

        if (storeRepository.existsByUserAccount(authService.getAuthentication()))
            errors.add(StoreResponseMessage.STORE_ACCOUNT_EXIST_ERROR);
        if (storeRepository.existsByNoSiup(noSiup)) errors.add(StoreResponseMessage.STORE_NO_SIUP_EXIST_ERROR);
        if (storeRepository.existsByPhoneNumber(phoneNumber)) errors.add(StoreResponseMessage.STORE_PHONE_NUMBER_EXIST_ERROR);

        return errors;
    }

    private Store toStore(StoreRequest request) {
        return Store.builder()
                .noSiup(request.getNoSiup())
                .name(request.getName())
                .address(request.getAddress())
                .phoneNumber(request.getPhoneNumber())
                .userAccount(authService.getAuthentication())
                .build();
    }

    private StoreResponse toStoreResponse(Store response) {
        return StoreResponse.builder()
                .id(response.getId())
                .noSiup(response.getNoSiup())
                .name(response.getName())
                .address(response.getAddress())
                .phoneNumber(response.getPhoneNumber())
                .userId(response.getUserAccount().getId())
                .build();
    }

    private StoreWithProductsResponse toStoreWithProductsResponse(Store response) {
        return StoreWithProductsResponse.builder()
                .id(response.getId())
                .noSiup(response.getNoSiup())
                .name(response.getName())
                .address(response.getAddress())
                .phoneNumber(response.getPhoneNumber())
                .products(
                        response.getProducts().stream()
                                .map(product -> (ProductResponse) ProductResponse.builder()
                                        .id(product.getId())
                                        .name(product.getName())
                                        .description(product.getDescription())
                                        .price(product.getPrice())
                                        .stock(product.getStock())
                                        .storeName(product.getStore().getName())
                                        .build()).toList()
                )
                .userId(response.getUserAccount().getId())
                .build();
    }
}
