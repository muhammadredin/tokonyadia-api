package io.github.muhammadredin.tokonyadiaapi.controller;

import io.github.muhammadredin.tokonyadiaapi.constant.APIPath;
import io.github.muhammadredin.tokonyadiaapi.constant.PaymentResponseMessage;
import io.github.muhammadredin.tokonyadiaapi.dto.request.CheckoutRequest;
import io.github.muhammadredin.tokonyadiaapi.dto.request.PagingAndSortingRequest;
import io.github.muhammadredin.tokonyadiaapi.dto.request.midtransRequest.MidtransNotification;
import io.github.muhammadredin.tokonyadiaapi.service.InvoiceService;
import io.github.muhammadredin.tokonyadiaapi.service.TransactionService;
import io.github.muhammadredin.tokonyadiaapi.util.ResponseUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(APIPath.PAYMENT_API)
@RequiredArgsConstructor
public class PaymentController {
    private final TransactionService transactionService;
    private final InvoiceService invoiceService;

    @PostMapping
    public ResponseEntity<?> checkoutForPaymentHandler(
            @RequestBody CheckoutRequest request
    ) {
        return ResponseUtil.buildResponse(HttpStatus.CREATED, PaymentResponseMessage.SUCCESS_CREATE_INVOICE, transactionService.checkoutCart(request));
    }

    @GetMapping
    public ResponseEntity<?> getAllCustomerInvoiceHandler(
            @RequestParam(required = false, defaultValue = "1") Integer page,
            @RequestParam(required = false, defaultValue = "10") Integer size,
            @RequestParam(required = false) String sort
    ) {
        PagingAndSortingRequest pagingAndSortingRequest = new PagingAndSortingRequest(page, size, sort);
        return ResponseUtil.buildResponsePaging(
                HttpStatus.OK,
                PaymentResponseMessage.SUCCESS_GET_ALL_CUSTOMER_INVOICES,
                invoiceService.getAllCustomerInvoice(pagingAndSortingRequest));
    }

    @GetMapping("/detail/{id}")
    public ResponseEntity<?> getCustomerPaymentDetailHandler(
            @PathVariable String id
    ) {
        return ResponseUtil.buildResponse(
                HttpStatus.OK,
                PaymentResponseMessage.SUCCESS_GET_CUSTOMER_PAYMENT_DETAIL,
                invoiceService.getCustomerPaymentDetail(id));
    }

    @PostMapping("/notification")
    public ResponseEntity<?> handleMidtransWebhook(@RequestBody MidtransNotification notification) {
        transactionService.updateInvoiceStatus(notification);
        return ResponseEntity.ok().body(notification);
    }
}
