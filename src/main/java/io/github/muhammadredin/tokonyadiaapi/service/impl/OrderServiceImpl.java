package io.github.muhammadredin.tokonyadiaapi.service.impl;

import io.github.muhammadredin.tokonyadiaapi.dto.response.OrderDetailResponse;
import io.github.muhammadredin.tokonyadiaapi.dto.response.ProductOrderResponse;
import io.github.muhammadredin.tokonyadiaapi.dto.response.StoreOrderResponse;
import io.github.muhammadredin.tokonyadiaapi.entity.Order;
import io.github.muhammadredin.tokonyadiaapi.entity.OrderDetails;
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
    private final StoreService storeService;

    @Override
    public Order createOrder(Order request) {
        return orderRepository.saveAndFlush(request);
    }

    @Transactional(readOnly = true)
    @Override
    public OrderDetailResponse getCustomerOrderById(String transactionId) {
        Order order = getOne(transactionId);

        UserAccount userAccount = (UserAccount) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (!order.getInvoice().getCustomer().equals(userAccount.getCustomer())) {
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
                .orderId(transactionId)
                .orderDate(order.getOrderDate())
                .orderStatus(order.getOrderStatus())
                .shippingProvider(order.getShippingProvider())
                .totalPrice(totalPrice)
                .productDetails(productDetails)
                .build();
    }

    @Override
    public Order getOne(String transactionId) {
        return orderRepository.findById(transactionId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Transaction not found"));
    }

    @Override
    public List<StoreOrderResponse> getAllTransactionDetailsByStoreId(String storeId) {
        Specification<Order> specification = OrderSpecification.storeTransactionDetails(storeService.getOne(storeId));
        return orderRepository.findAll(specification).stream()
                .map(o -> {
                    return StoreOrderResponse.builder()
                            .orderId(o.getId())
                            .customerName(o.getInvoice().getCustomer().getName())
                            .transactionStatus(o.getOrderStatus().name())
                            .build();
                })
                .toList();
    }
}
