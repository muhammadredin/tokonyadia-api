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
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class InvoiceServiceImpl implements InvoiceService {
    private final InvoiceRepository invoiceRepository;

    @Transactional(rollbackFor = Exception.class)
    @Override
    public Invoice createInvoice(Invoice request) {
        log.info("Creating new invoice for request: {}", request);
        Invoice savedInvoice = invoiceRepository.saveAndFlush(request);
        log.info("Invoice created with ID: {}", savedInvoice.getId());
        return savedInvoice;
    }

    @Transactional(readOnly = true)
    @Override
    public Page<InvoiceResponse> getAllCustomerInvoice(PagingAndSortingRequest request) {
        log.info("Fetching all invoices for the current customer");
        UserAccount userAccount = (UserAccount) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Customer customer = userAccount.getCustomer();

        Sort sortBy = Sort.unsorted();
        if (request.getSort() != null) {
            sortBy = SortUtil.getSort(request.getSort());
        }
        Pageable pageable = PagingUtil.getPageable(request, sortBy);

        Page<InvoiceResponse> invoices = invoiceRepository.getInvoiceByCustomer(customer, pageable).map(this::toInvoiceResponse);
        log.info("Found {} invoices for customer ID: {}", invoices.getTotalElements(), customer.getId());
        return invoices;
    }

    @Transactional(readOnly = true)
    @Override
    public PaymentDetailResponse getCustomerPaymentDetail(String id) {
        log.info("Fetching payment details for invoice ID: {}", id);
        UserAccount userAccount = (UserAccount) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Customer customer = userAccount.getCustomer();
        Invoice invoice = getOne(id);

        if (!invoice.getCustomer().getId().equals(customer.getId())) {
            log.warn("Customer ID mismatch: Invoice ID: {} does not belong to customer ID: {}", id, customer.getId());
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, InvoiceResponseMessage.ERROR_CUSTOMER_INVOICE_NOT_FOUND);
        }

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

        PaymentDetailResponse paymentDetailResponse = PaymentDetailResponse.builder()
                .totalPrice(invoice.getGrossAmount())
                .paymentMethod(invoice.getPaymentType().name())
                .orders(orderList)
                .build();

        log.info("Payment details fetched successfully for invoice ID: {}", id);
        return paymentDetailResponse;
    }

    @Transactional(readOnly = true)
    @Override
    public Invoice getOne(String id) {
        log.info("Retrieving invoice with ID: {}", id);
        Invoice invoice = invoiceRepository.findById(id).orElseThrow();
        log.info("Invoice found: {}", invoice);
        return invoice;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public Invoice setInvoiceStatus(String invoiceId, String transactionStatus) {
        log.info("Updating status for invoice ID: {} to {}", invoiceId, transactionStatus);
        Invoice invoice = getOne(invoiceId);
        invoice.setTransactionStatus(TransactionStatus.fromDescription(transactionStatus));
        Invoice updatedInvoice = invoiceRepository.saveAndFlush(invoice);
        log.info("Invoice ID: {} updated with status: {}", invoiceId, transactionStatus);
        return updatedInvoice;
    }

    public InvoiceResponse toInvoiceResponse(Invoice invoice) {
        log.debug("Converting invoice to response: {}", invoice.getId());
        return InvoiceResponse.builder()
                .id(invoice.getId())
                .expiryDate(invoice.getExpiryTime())
                .paymentType(invoice.getPaymentType())
                .grossAmount(invoice.getGrossAmount())
                .transactionStatus(invoice.getTransactionStatus())
                .build();
    }
}
