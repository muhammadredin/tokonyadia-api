package io.github.muhammadredin.tokonyadiaapi.service;

import io.github.muhammadredin.tokonyadiaapi.dto.request.CheckoutRequest;
import io.github.muhammadredin.tokonyadiaapi.dto.request.midtransRequest.MidtransNotification;
import io.github.muhammadredin.tokonyadiaapi.dto.response.InvoiceResponse;
import io.github.muhammadredin.tokonyadiaapi.dto.response.midtransResponse.MidtransSnapResponse;
import org.springframework.transaction.annotation.Transactional;

public interface TransactionService {
    @Transactional(rollbackFor = Exception.class)
    InvoiceResponse checkoutCart(CheckoutRequest request);

    @Transactional(rollbackFor = Exception.class)
    void updateInvoiceStatus(MidtransNotification notification);
}
