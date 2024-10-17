package io.github.muhammadredin.tokonyadiaapi.service;

import io.github.muhammadredin.tokonyadiaapi.dto.request.StoreOrderDetailResponse;
import io.github.muhammadredin.tokonyadiaapi.dto.response.OrderDetailResponse;
import io.github.muhammadredin.tokonyadiaapi.dto.response.StoreOrderResponse;
import io.github.muhammadredin.tokonyadiaapi.entity.Order;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface OrderService {
    Order createOrder(Order request);

    @Transactional(readOnly = true)
    OrderDetailResponse getCustomerOrderById(String orderId);

    Order getOne(String transactionId);

    List<StoreOrderResponse> getAllOrderByStoreId(String storeId);

    void updateOrderStatus(Order order);

    StoreOrderDetailResponse getOrderDetailByStoreId(String orderId);
}
