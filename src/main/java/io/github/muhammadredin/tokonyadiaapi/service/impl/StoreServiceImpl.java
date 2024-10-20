package io.github.muhammadredin.tokonyadiaapi.service.impl;

import io.github.muhammadredin.tokonyadiaapi.constant.OrderStatus;
import io.github.muhammadredin.tokonyadiaapi.constant.StoreResponseMessage;
import io.github.muhammadredin.tokonyadiaapi.dto.request.ProductRequest;
import io.github.muhammadredin.tokonyadiaapi.dto.request.SearchStoreRequest;
import io.github.muhammadredin.tokonyadiaapi.dto.request.StoreRequest;
import io.github.muhammadredin.tokonyadiaapi.dto.response.ProductResponse;
import io.github.muhammadredin.tokonyadiaapi.dto.response.StoreOrderResponse;
import io.github.muhammadredin.tokonyadiaapi.dto.response.StoreResponse;
import io.github.muhammadredin.tokonyadiaapi.dto.response.StoreWithProductsResponse;
import io.github.muhammadredin.tokonyadiaapi.entity.*;
import io.github.muhammadredin.tokonyadiaapi.repository.StoreRepository;
import io.github.muhammadredin.tokonyadiaapi.service.AuthService;
import io.github.muhammadredin.tokonyadiaapi.service.OrderService;
import io.github.muhammadredin.tokonyadiaapi.service.ProductService;
import io.github.muhammadredin.tokonyadiaapi.service.StoreService;
import io.github.muhammadredin.tokonyadiaapi.specification.OrderSpecification;
import io.github.muhammadredin.tokonyadiaapi.specification.StoreSpecification;
import io.github.muhammadredin.tokonyadiaapi.util.PagingUtil;
import io.github.muhammadredin.tokonyadiaapi.util.SortUtil;
import io.github.muhammadredin.tokonyadiaapi.util.ValidationUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class StoreServiceImpl implements StoreService {
    private final StoreRepository storeRepository;
    private final ProductService productService;
    private final OrderService orderService;
    private final ValidationUtil validationUtil;

    @Transactional(rollbackFor = Exception.class)
    @Override
    public StoreResponse createStore(StoreRequest request) {
        validationUtil.validate(request);
        List<String> errors = checkStore(request.getNoSiup(), request.getName());

        if (!errors.isEmpty()) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, errors.toString());

        return toStoreResponse(storeRepository.save(toStore(request)));
    }

    @Transactional(readOnly = true)
    @Override
    public StoreResponse getStoreById(String id) {
        Store store = getOne(id);
        return toStoreResponse(store);
    }

    @Transactional(readOnly = true)
    @Override
    public Store getOne(String id) {
        return storeRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, StoreResponseMessage.STORE_NOT_FOUND_ERROR));
    }

    @Transactional(readOnly = true)
    @Override
    public Page<StoreResponse> getAllStore(SearchStoreRequest request) {
        Sort sortBy = SortUtil.getSort(request.getSort());

        Specification<Store> specification = StoreSpecification.store(request);
        Page<Store> stores = storeRepository.findAll(specification, PagingUtil.getPageable(request, sortBy));

        return stores.map(this::toStoreResponse);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public StoreResponse updateStore(String id, StoreRequest request) {
        validationUtil.validate(request);
        Store store = getOne(id);
        store.setNoSiup(request.getNoSiup());
        store.setName(request.getName());
        store.setAddress(request.getAddress());
        store.setPhoneNumber(request.getPhoneNumber());
        return toStoreResponse(storeRepository.save(store));
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void deleteStore(String id) {
        storeRepository.delete(getOne(id));
    }

    @Transactional(readOnly = true)
    @Override
    public List<StoreOrderResponse> getAllStoreOrders(String storeId) {
        Specification<Order> specification = OrderSpecification.storeTransactionDetails(getOne(storeId));
        return orderService.getOrdersBySpecification(specification).stream()
                .map(o -> {
                    return StoreOrderResponse.builder()
                            .orderId(o.getId())
                            .customerName(o.getInvoice().getCustomer().getName())
                            .orderStatus(o.getOrderStatus().name())
                            .build();
                })
                .toList();
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void processOrder(String orderId) {
        Order order = orderService.getOne(orderId);
        if (order.getOrderStatus() != OrderStatus.VERIFIED) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Cannot process order with unverified payment");

        order.setOrderStatus(OrderStatus.ON_PROCESS);
        orderService.updateOrderStatus(order);

        for (OrderDetails orderDetails : order.getOrderDetails()) {
            Product product = orderDetails.getProduct();
            ProductRequest productRequest = ProductRequest.builder()
                    .name(product.getName())
                    .description(product.getDescription())
                    .price(product.getPrice())
                    .stock(product.getStock() - orderDetails.getQuantity())
                    .build();

            productService.updateProduct(product.getId(), productRequest);
        }
    }

    private List<String> checkStore(String noSiup, String phoneNumber) {
        List<String> errors = new ArrayList<>();
        UserAccount userAccount = (UserAccount) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (storeRepository.existsByUserAccount(userAccount))
            errors.add(StoreResponseMessage.STORE_ACCOUNT_EXIST_ERROR);
        if (storeRepository.existsByNoSiup(noSiup)) errors.add(StoreResponseMessage.STORE_NO_SIUP_EXIST_ERROR);
        if (storeRepository.existsByPhoneNumber(phoneNumber)) errors.add(StoreResponseMessage.STORE_PHONE_NUMBER_EXIST_ERROR);

        return errors;
    }

    private Store toStore(StoreRequest request) {
        UserAccount userAccount = (UserAccount) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return Store.builder()
                .noSiup(request.getNoSiup())
                .name(request.getName())
                .address(request.getAddress())
                .phoneNumber(request.getPhoneNumber())
                .userAccount(userAccount)
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
                                        .productId(product.getId())
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
