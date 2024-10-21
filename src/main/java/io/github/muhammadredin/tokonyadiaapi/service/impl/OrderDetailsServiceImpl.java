package io.github.muhammadredin.tokonyadiaapi.service.impl;

import io.github.muhammadredin.tokonyadiaapi.entity.OrderDetails;
import io.github.muhammadredin.tokonyadiaapi.repository.OrderDetailsRepository;
import io.github.muhammadredin.tokonyadiaapi.service.OrderDetailsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderDetailsServiceImpl implements OrderDetailsService {
    private final OrderDetailsRepository orderDetailsRepository;

    @Transactional(rollbackFor = Exception.class)
    @Override
    public OrderDetails createOrderDetails(OrderDetails request) {
        log.info("Creating order details: {}", request);

        // Save the order details and flush the changes to the database
        OrderDetails savedOrderDetails = orderDetailsRepository.saveAndFlush(request);

        log.info("Order details created successfully with ID: {}", savedOrderDetails.getId());
        return savedOrderDetails;
    }
}
