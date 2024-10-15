package io.github.muhammadredin.tokonyadiaapi.service;

import io.github.muhammadredin.tokonyadiaapi.dto.request.PagingAndSortingRequest;
import io.github.muhammadredin.tokonyadiaapi.dto.request.PaymentDetailResponse;
import io.github.muhammadredin.tokonyadiaapi.dto.response.InvoiceResponse;
import io.github.muhammadredin.tokonyadiaapi.entity.Invoice;
import org.springframework.data.domain.Page;

public interface InvoiceService {
    Invoice createInvoice(Invoice request);

    Page<InvoiceResponse> getAllCustomerInvoice(PagingAndSortingRequest request);

    PaymentDetailResponse getCustomerPaymentDetail(String id);

    Invoice getOne(String id);
}
