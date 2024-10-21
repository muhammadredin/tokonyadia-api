package io.github.muhammadredin.tokonyadiaapi.service.impl;

import io.github.muhammadredin.tokonyadiaapi.constant.OrderResponseMessage;
import io.github.muhammadredin.tokonyadiaapi.constant.OrderStatus;
import io.github.muhammadredin.tokonyadiaapi.dto.request.StoreOrderDetailResponse;
import io.github.muhammadredin.tokonyadiaapi.dto.response.OrderDetailResponse;
import io.github.muhammadredin.tokonyadiaapi.dto.response.ProductOrderResponse;
import io.github.muhammadredin.tokonyadiaapi.entity.Order;
import io.github.muhammadredin.tokonyadiaapi.entity.OrderDetails;
import io.github.muhammadredin.tokonyadiaapi.entity.Store;
import io.github.muhammadredin.tokonyadiaapi.entity.UserAccount;
import io.github.muhammadredin.tokonyadiaapi.repository.OrderRepository;
import io.github.muhammadredin.tokonyadiaapi.service.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
@Slf4j
public class OrderServiceImpl implements OrderService {
    private final OrderRepository orderRepository;

    @Transactional(rollbackFor = Exception.class)
    @Override
    public Order createOrder(Order request) {
        log.info("Creating order with details: {}", request);
        Order savedOrder = orderRepository.saveAndFlush(request);
        log.info("Order created successfully with ID: {}", savedOrder.getId());
        return savedOrder;
    }

    @Transactional(readOnly = true)
    @Override
    public OrderDetailResponse getCustomerOrderById(String orderId) {
        log.info("Fetching order details for order ID: {}", orderId);
        Order order = getOne(orderId);

        UserAccount userAccount = (UserAccount) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (!order.getInvoice().getCustomer().getId().equals(userAccount.getCustomer().getId())) {
            log.warn("Access denied for user {} to order ID: {}", userAccount.getUsername(), orderId);
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Access Denied");
        }

        List<ProductOrderResponse> productDetails = new ArrayList<>();
        int totalPrice = 0;

        for (OrderDetails orderDetails : order.getOrderDetails()) {
            totalPrice += orderDetails.getPrice() * orderDetails.getQuantity();

            ProductOrderResponse productOrderResponse = ProductOrderResponse.builder()
                    .productId(orderDetails.getProduct().getId())
                    .productName(orderDetails.getProduct().getName())
                    .productPrice(orderDetails.getPrice())
                    .quantity(orderDetails.getQuantity())
                    .build();

            productDetails.add(productOrderResponse);
        }

        log.info("Order details fetched successfully for order ID: {}", orderId);
        return OrderDetailResponse.builder()
                .orderId(orderId)
                .orderDate(order.getOrderDate())
                .orderStatus(order.getOrderStatus())
                .shippingProvider(order.getShippingProvider())
                .totalPrice(totalPrice)
                .shippingAddress(order.getInvoice().getCustomer().getAddress())
                .productDetails(productDetails)
                .build();
    }

    @Transactional(readOnly = true)
    @Override
    public Order getOne(String orderId) {
        log.info("Retrieving order with ID: {}", orderId);
        return orderRepository.findById(orderId)
                .orElseThrow(() -> {
                    log.error("Order not found for ID: {}", orderId);
                    return new ResponseStatusException(HttpStatus.NOT_FOUND, "Transaction not found");
                });
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void updateOrderStatus(Order order) {
        log.info("Updating order status for order ID: {}", order.getId());
        orderRepository.save(order);
        log.info("Order status updated successfully for order ID: {}", order.getId());
    }

    @Transactional(readOnly = true)
    @Override
    public List<Order> getOrdersBySpecification(Specification<Order> specification) {
        log.info("Fetching orders with specified criteria");
        return orderRepository.findAll(specification);
    }

    @Transactional(readOnly = true)
    @Override
    public StoreOrderDetailResponse getOrderDetailByStoreId(String orderId) {
        log.info("Fetching order details for store ID with order ID: {}", orderId);
        UserAccount userAccount = (UserAccount) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Store store = userAccount.getStore();
        Order order = getOne(orderId);

        if (!order.getOrderDetails().get(0).getProduct().getStore().getId().equals(store.getId())) {
            log.warn("Order not found for store ID: {}", store.getId());
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, OrderResponseMessage.ERROR_ORDER_NOT_FOUND);
        }

        List<ProductOrderResponse> productDetails = new ArrayList<>();
        int totalPrice = 0;

        for (OrderDetails orderDetails : order.getOrderDetails()) {
            totalPrice += orderDetails.getPrice() * orderDetails.getQuantity();

            ProductOrderResponse productOrderResponse = ProductOrderResponse.builder()
                    .productId(orderDetails.getProduct().getId())
                    .productName(orderDetails.getProduct().getName())
                    .productPrice(orderDetails.getPrice())
                    .quantity(orderDetails.getQuantity())
                    .build();

            productDetails.add(productOrderResponse);
        }

        log.info("Order details fetched successfully for order ID: {}", orderId);
        return StoreOrderDetailResponse.builder()
                .customerId(order.getInvoice().getCustomer().getId())
                .customerName(order.getInvoice().getCustomer().getName())
                .orderId(order.getId())
                .orderStatus(order.getOrderStatus())
                .orderDate(order.getOrderDate())
                .totalPrice(totalPrice)
                .shippingAddress(order.getInvoice().getCustomer().getAddress())
                .shippingProvider(order.getShippingProvider())
                .productDetails(productDetails)
                .build();
    }
}
