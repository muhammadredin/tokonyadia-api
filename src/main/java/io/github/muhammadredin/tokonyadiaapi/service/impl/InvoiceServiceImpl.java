package io.github.muhammadredin.tokonyadiaapi.service.impl;

import io.github.muhammadredin.tokonyadiaapi.dto.request.PagingAndSortingRequest;
import io.github.muhammadredin.tokonyadiaapi.dto.response.InvoiceResponse;
import io.github.muhammadredin.tokonyadiaapi.entity.Customer;
import io.github.muhammadredin.tokonyadiaapi.entity.Invoice;
import io.github.muhammadredin.tokonyadiaapi.entity.UserAccount;
import io.github.muhammadredin.tokonyadiaapi.repository.InvoiceRepository;
import io.github.muhammadredin.tokonyadiaapi.service.InvoiceService;
import io.github.muhammadredin.tokonyadiaapi.util.PagingUtil;
import io.github.muhammadredin.tokonyadiaapi.util.SortUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class InvoiceServiceImpl implements InvoiceService {
    private final InvoiceRepository invoiceRepository;

    @Override
    public Invoice createInvoice(Invoice request) {
        return invoiceRepository.saveAndFlush(request);
    }

    @Override
    public Page<InvoiceResponse> getAllCustomerInvoice(PagingAndSortingRequest request) {
        UserAccount userAccount = (UserAccount) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Customer customer = userAccount.getCustomer();

        Sort sortBy = Sort.unsorted();
        if (request.getSort() != null) {
            sortBy = SortUtil.getSort(request.getSort());
        }

        Pageable pageable = PagingUtil.getPageable(request, sortBy);

        return invoiceRepository.getInvoiceByCustomer(customer, pageable).map(this::toInvoiceResponse);
    }

//    TODO: Ganti return menjadi InvoiceDetailRespone
    @Override
    public InvoiceResponse getCustomerInvoiceById(String id) {
        UserAccount userAccount = (UserAccount) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Customer customer = userAccount.getCustomer();

        return toInvoiceResponse(invoiceRepository.getInvoiceByCustomerAndId(customer, id));
    }

    private InvoiceResponse toInvoiceResponse(Invoice invoice) {
        return InvoiceResponse.builder()
                .id(invoice.getId())
                .paymentDueDate(invoice.getPaymentDueDate())
                .paymentCode(invoice.getPaymentCode())
                .paymentMethod(invoice.getPaymentMethod())

                .totalPayment(invoice.getTotalPayment())
                .status(invoice.getPaymentStatus())
                .build();
    }
}
