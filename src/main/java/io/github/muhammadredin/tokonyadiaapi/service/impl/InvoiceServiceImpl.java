package io.github.muhammadredin.tokonyadiaapi.service.impl;

import io.github.muhammadredin.tokonyadiaapi.constant.InvoiceResponseMessage;
import io.github.muhammadredin.tokonyadiaapi.constant.TransactionStatus;
import io.github.muhammadredin.tokonyadiaapi.dto.request.PagingAndSortingRequest;
import io.github.muhammadredin.tokonyadiaapi.dto.request.PaymentDetailResponse;
import io.github.muhammadredin.tokonyadiaapi.dto.response.InvoiceResponse;
import io.github.muhammadredin.tokonyadiaapi.dto.response.PaymentOrderResponse;
import io.github.muhammadredin.tokonyadiaapi.dto.response.ProductOrderResponse;
import io.github.muhammadredin.tokonyadiaapi.entity.*;
import io.github.muhammadredin.tokonyadiaapi.repository.InvoiceRepository;
import io.github.muhammadredin.tokonyadiaapi.service.InvoiceService;
import io.github.muhammadredin.tokonyadiaapi.util.PagingUtil;
import io.github.muhammadredin.tokonyadiaapi.util.SortUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
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

    @Override
    public PaymentDetailResponse getCustomerPaymentDetail(String id) {
        UserAccount userAccount = (UserAccount) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Customer customer = userAccount.getCustomer();

        Invoice invoice = getOne(id);

        if (!invoice.getCustomer().getId().equals(customer.getId()))
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, InvoiceResponseMessage.ERROR_CUSTOMER_INVOICE_NOT_FOUND);

        List<PaymentOrderResponse> orderList = new ArrayList<>();

        for (Order order : invoice.getOrder()) {
            List<ProductOrderResponse> productOrderList = new ArrayList<>();
            PaymentOrderResponse orderResponse = PaymentOrderResponse.builder()
                    .storeName(order.getOrderDetails().get(0).getProduct().getStore().getName())
                    .shippingProvider(order.getShippingProvider().name())
                    .build();
            for (OrderDetails orderDetail : order.getOrderDetails()) {
                ProductOrderResponse product = ProductOrderResponse.builder()
                        .productId(orderDetail.getProduct().getId())
                        .productName(orderDetail.getProduct().getName())
                        .productPrice(orderDetail.getProduct().getPrice())
                        .quantity(orderDetail.getQuantity())
                        .build();
                productOrderList.add(product);
            }
            orderResponse.setProducts(productOrderList);
            orderList.add(orderResponse);
        }

        return PaymentDetailResponse.builder()
                .totalPrice(invoice.getGrossAmount())
                .paymentMethod(invoice.getPaymentType().name())
                .orders(orderList)
                .build();
    }

    @Override
    public Invoice getOne(String id) {
        return invoiceRepository.findById(id).orElseThrow();
    }

    @Override
    public Invoice setInvoiceStatus(String invoiceId, String transactionStatus) {
        Invoice invoice = getOne(invoiceId);
        invoice.setTransactionStatus(TransactionStatus.fromDescription(transactionStatus));
        return invoiceRepository.saveAndFlush(invoice);
    }

    public InvoiceResponse toInvoiceResponse(Invoice invoice) {
        return InvoiceResponse.builder()
                .id(invoice.getId())
                .expiryDate(invoice.getExpiryTime())
                .paymentType(invoice.getPaymentType())
                .grossAmount(invoice.getGrossAmount())
                .transactionStatus(invoice.getTransactionStatus())
                .build();
    }
}
