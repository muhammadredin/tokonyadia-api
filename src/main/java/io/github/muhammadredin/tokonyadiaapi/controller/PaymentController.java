package io.github.muhammadredin.tokonyadiaapi.controller;

import io.github.muhammadredin.tokonyadiaapi.dto.request.CheckoutRequest;
import io.github.muhammadredin.tokonyadiaapi.dto.request.PagingAndSortingRequest;
import io.github.muhammadredin.tokonyadiaapi.service.InvoiceService;
import io.github.muhammadredin.tokonyadiaapi.service.impl.CheckoutService;
import io.github.muhammadredin.tokonyadiaapi.util.ResponseUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/payment")
@RequiredArgsConstructor
public class PaymentController {
    private final CheckoutService checkoutService;
    private final InvoiceService invoiceService;

    @PostMapping
    public ResponseEntity<?> checkoutForPaymentHandler(
            @RequestBody CheckoutRequest request
    ) {
        return ResponseUtil.buildResponse(HttpStatus.CREATED, "Transaction created", checkoutService.checkoutCart(request));
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
                "Success fething all customer invoice",
                invoiceService.getAllCustomerInvoice(pagingAndSortingRequest));
    }
}
