package io.github.muhammadredin.tokonyadiaapi.service;

import io.github.muhammadredin.tokonyadiaapi.dto.response.OrderResponse;
import io.github.muhammadredin.tokonyadiaapi.dto.response.StoreOrderResponse;
import io.github.muhammadredin.tokonyadiaapi.entity.Order;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface OrderService {
    Order createOrder(Order request);

    @Transactional(readOnly = true)
    OrderResponse getCustomerOrderById(String transactionId);

    Order getOne(String transactionId);

    List<StoreOrderResponse> getAllTransactionDetailsByStoreId(String storeId);
}
