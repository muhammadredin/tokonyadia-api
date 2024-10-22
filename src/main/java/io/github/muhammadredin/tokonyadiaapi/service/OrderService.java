package io.github.muhammadredin.tokonyadiaapi.service;

import io.github.muhammadredin.tokonyadiaapi.constant.OrderStatus;
import io.github.muhammadredin.tokonyadiaapi.dto.request.StoreOrderDetailResponse;
import io.github.muhammadredin.tokonyadiaapi.dto.response.OrderDetailResponse;
import io.github.muhammadredin.tokonyadiaapi.entity.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface OrderService {
    Order createOrder(Order request);

    @Transactional(readOnly = true)
    OrderDetailResponse getCustomerOrderById(String orderId);

    Order getOne(String transactionId);

    void updateOrderStatus(Order order);

    Page<Order> getOrdersBySpecification(Specification<Order> specification, Pageable pageable);

    StoreOrderDetailResponse getOrderDetailByStoreId(String orderId);
}
