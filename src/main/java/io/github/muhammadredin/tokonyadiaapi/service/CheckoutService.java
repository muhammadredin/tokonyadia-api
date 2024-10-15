package io.github.muhammadredin.tokonyadiaapi.service;

import io.github.muhammadredin.tokonyadiaapi.dto.request.CheckoutRequest;
import io.github.muhammadredin.tokonyadiaapi.dto.response.InvoiceResponse;
import org.springframework.transaction.annotation.Transactional;

public interface CheckoutService {
    @Transactional(rollbackFor = Exception.class)
    InvoiceResponse checkoutCart(CheckoutRequest request);
}
