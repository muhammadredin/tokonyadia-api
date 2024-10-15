package io.github.muhammadredin.tokonyadiaapi.service.impl;

import io.github.muhammadredin.tokonyadiaapi.constant.OrderResponseMessage;
import io.github.muhammadredin.tokonyadiaapi.dto.request.StoreOrderDetailResponse;
import io.github.muhammadredin.tokonyadiaapi.dto.response.OrderDetailResponse;
import io.github.muhammadredin.tokonyadiaapi.dto.response.ProductOrderResponse;
import io.github.muhammadredin.tokonyadiaapi.dto.response.StoreOrderResponse;
import io.github.muhammadredin.tokonyadiaapi.entity.Order;
import io.github.muhammadredin.tokonyadiaapi.entity.OrderDetails;
import io.github.muhammadredin.tokonyadiaapi.entity.Store;
import io.github.muhammadredin.tokonyadiaapi.entity.UserAccount;
import io.github.muhammadredin.tokonyadiaapi.repository.OrderRepository;
import io.github.muhammadredin.tokonyadiaapi.service.*;
import io.github.muhammadredin.tokonyadiaapi.specification.OrderSpecification;
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
    private final StoreService storeService;;

    @Override
    public Order createOrder(Order request) {
        return orderRepository.saveAndFlush(request);
    }

    @Transactional(readOnly = true)
    @Override
    public OrderDetailResponse getCustomerOrderById(String orderId) {
        Order order = getOne(orderId);

        UserAccount userAccount = (UserAccount) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (!order.getInvoice().getCustomer().getId().equals(userAccount.getCustomer().getId())) {
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

    @Override
    public Order getOne(String orderId) {
        return orderRepository.findById(orderId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Transaction not found"));
    }

    @Override
    public List<StoreOrderResponse> getAllOrderByStoreId(String storeId) {
        Specification<Order> specification = OrderSpecification.storeTransactionDetails(storeService.getOne(storeId));
        return orderRepository.findAll(specification).stream()
                .map(o -> {
                    return StoreOrderResponse.builder()
                            .orderId(o.getId())
                            .customerName(o.getInvoice().getCustomer().getName())
                            .orderStatus(o.getOrderStatus().name())
                            .build();
                })
                .toList();
    }

    @Override
    public StoreOrderDetailResponse getOrderDetailByStoreId(String orderId) {
        UserAccount userAccount = (UserAccount) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Store store = userAccount.getStore();
        Order order = getOne(orderId);

        if (!order.getOrderDetails().get(0).getProduct().getStore().getId().equals(store.getId()))
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, OrderResponseMessage.ERROR_ORDER_NOT_FOUND);

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
