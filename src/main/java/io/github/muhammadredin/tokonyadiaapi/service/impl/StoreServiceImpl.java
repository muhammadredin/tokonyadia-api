package io.github.muhammadredin.tokonyadiaapi.service.impl;


import io.github.muhammadredin.tokonyadiaapi.entity.Store;
import io.github.muhammadredin.tokonyadiaapi.repository.StoreRepository;
import io.github.muhammadredin.tokonyadiaapi.service.StoreService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class StoreServiceImpl implements StoreService {
    private final StoreRepository storeRepository;

    @Autowired
    public StoreServiceImpl(StoreRepository storeRepository) {
        this.storeRepository = storeRepository;
    }

    @Override
    public Store createStore(Store store) {
        return storeRepository.save(store);
    }

    @Override
    public Store getStoreById(String id) {
        Store store = storeRepository.findById(id).orElse(null);
        if (store == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Customer not found");
        }
        return store;
    }

    @Override
    public List<Store> getAllStore() {
        return storeRepository.findAll();
    }

    @Override
    public Store updateStore(String id, Store store) {
        Store getStore = storeRepository.findById(id).orElse(null);
        if (getStore == null) {
            throw new RuntimeException("Store not found");
        }
        getStore.setNoSiup(store.getNoSiup());
        getStore.setName(store.getName());
        getStore.setAddress(store.getAddress());
        getStore.setPhoneNumber(store.getPhoneNumber());
        return storeRepository.save(getStore);
    }

    @Override
    public void deleteStore(String id) {
        Store store = storeRepository.findById(id).orElse(null);
        if (store == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Customer not found");
        }
        storeRepository.delete(store);
    }
}
