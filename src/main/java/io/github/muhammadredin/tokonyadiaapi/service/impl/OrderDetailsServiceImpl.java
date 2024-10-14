package io.github.muhammadredin.tokonyadiaapi.service.impl;

import io.github.muhammadredin.tokonyadiaapi.entity.OrderDetails;
import io.github.muhammadredin.tokonyadiaapi.repository.OrderDetailsRepository;
import io.github.muhammadredin.tokonyadiaapi.service.OrderDetailsService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OrderDetailsServiceImpl implements OrderDetailsService {
    private final OrderDetailsRepository orderDetailsRepository;

    @Override
    public OrderDetails createOrderDetails(OrderDetails request) {
       return orderDetailsRepository.saveAndFlush(request);
    }
}
