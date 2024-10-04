package io.github.muhammadredin.tokonyadiaapi.controller;

import io.github.muhammadredin.tokonyadiaapi.dto.request.CustomerRequest;
import io.github.muhammadredin.tokonyadiaapi.dto.response.CustomerResponse;
import io.github.muhammadredin.tokonyadiaapi.entity.Customer;
import io.github.muhammadredin.tokonyadiaapi.entity.Store;
import io.github.muhammadredin.tokonyadiaapi.service.StoreService;
import org.springframework.beans.factory.annotation.Autowired;
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
    public List<Store> getAllCustomersHandler() {
        return storeService.getAllStore();
    }

    @GetMapping("/{id}")
    public Store getCustomerById(
            @PathVariable String id
    ) {
        return storeService.getStoreById(id);
    }

    @PostMapping
    public Store createCustomer(
            @RequestBody Store store
    ) {
        return storeService.createStore(store);
    }

    @PutMapping("/{id}")
    public Store updateCustomer(
            @PathVariable String id,
            @RequestBody Store store
    ) {
        return storeService.updateStore(id, store);
    }

    @DeleteMapping("/{id}")
    public String deleteCustomer(
            @PathVariable String id
    ) {
        storeService.deleteStore(id);
        return "Store successfully deleted";
    }
}
