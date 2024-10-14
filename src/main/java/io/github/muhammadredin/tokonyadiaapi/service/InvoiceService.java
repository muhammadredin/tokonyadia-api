package io.github.muhammadredin.tokonyadiaapi.service;

import io.github.muhammadredin.tokonyadiaapi.dto.request.InvoiceRequest;
import io.github.muhammadredin.tokonyadiaapi.dto.request.PagingAndSortingRequest;
import io.github.muhammadredin.tokonyadiaapi.dto.response.InvoiceResponse;
import io.github.muhammadredin.tokonyadiaapi.entity.Invoice;
import org.springframework.data.domain.Page;

import java.util.List;

public interface InvoiceService {
    Invoice createInvoice(Invoice request);

    Page<InvoiceResponse> getAllCustomerInvoice(PagingAndSortingRequest request);

    InvoiceResponse getCustomerInvoiceById(String id);
}
