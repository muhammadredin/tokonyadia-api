package io.github.muhammadredin.tokonyadiaapi.service.impl;

import io.github.muhammadredin.tokonyadiaapi.entity.OrderDetails;
import io.github.muhammadredin.tokonyadiaapi.repository.OrderDetailsRepository;
import io.github.muhammadredin.tokonyadiaapi.service.OrderDetailsService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class OrderDetailsServiceImpl implements OrderDetailsService {
    private final OrderDetailsRepository orderDetailsRepository;

    @Transactional(rollbackFor = Exception.class)
    @Override
    public OrderDetails createOrderDetails(OrderDetails request) {
       return orderDetailsRepository.saveAndFlush(request);
    }
}
