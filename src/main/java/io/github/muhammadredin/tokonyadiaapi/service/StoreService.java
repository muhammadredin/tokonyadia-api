package io.github.muhammadredin.tokonyadiaapi.service;


import io.github.muhammadredin.tokonyadiaapi.entity.Store;

import java.util.List;

public interface StoreService {
    Store createStore(Store store);

    Store getStoreById(String id);

    List<Store> getAllStore();

    Store updateStore(String id, Store store);

    void deleteStore(String id);
}
