package io.github.muhammadredin.tokonyadiaapi.service.impl;

import io.github.muhammadredin.tokonyadiaapi.dto.request.SearchStoreRequest;
import io.github.muhammadredin.tokonyadiaapi.dto.request.StoreRequest;
import io.github.muhammadredin.tokonyadiaapi.dto.response.StoreResponse;
import io.github.muhammadredin.tokonyadiaapi.entity.Store;
import io.github.muhammadredin.tokonyadiaapi.repository.StoreRepository;
import io.github.muhammadredin.tokonyadiaapi.service.StoreService;
import io.github.muhammadredin.tokonyadiaapi.specification.StoreSpecification;
import io.github.muhammadredin.tokonyadiaapi.util.PagingUtil;
import io.github.muhammadredin.tokonyadiaapi.util.SortUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class StoreServiceImpl implements StoreService {
    private final StoreRepository storeRepository;

    @Autowired
    public StoreServiceImpl(StoreRepository storeRepository) {
        this.storeRepository = storeRepository;
    }

    @Override
    public StoreResponse createStore(StoreRequest store) {
        return toStoreResponse(storeRepository.save(toStore(store)));
    }

    @Override
    public StoreResponse getStoreById(String id) {
        Store store = getOne(id);
        return toStoreResponse(store);
    }

    @Override
    public Store getOne(String id) {
        return storeRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Store not found"));
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

    private Store toStore(StoreRequest request) {
        return Store.builder()
                .noSiup(request.getNoSiup())
                .name(request.getName())
                .address(request.getAddress())
                .phoneNumber(request.getPhoneNumber())
                .build();
    }

    private StoreResponse toStoreResponse(Store response) {
        return StoreResponse.builder()
                .id(response.getId())
                .noSiup(response.getNoSiup())
                .name(response.getName())
                .address(response.getAddress())
                .phoneNumber(response.getPhoneNumber())
                .build();
    }
}
