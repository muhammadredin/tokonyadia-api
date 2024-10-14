package io.github.muhammadredin.tokonyadiaapi.service.impl;

import io.github.muhammadredin.tokonyadiaapi.constant.PaymentMethod;
import io.github.muhammadredin.tokonyadiaapi.constant.ShippingProvider;
import io.github.muhammadredin.tokonyadiaapi.dto.request.CheckoutRequest;
import io.github.muhammadredin.tokonyadiaapi.dto.request.OrderDetailsRequest;
import io.github.muhammadredin.tokonyadiaapi.dto.request.OrderRequest;
import io.github.muhammadredin.tokonyadiaapi.dto.response.InvoiceResponse;
import io.github.muhammadredin.tokonyadiaapi.entity.*;
import io.github.muhammadredin.tokonyadiaapi.service.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class CheckoutServiceImpl implements CheckoutService {
    private final OrderService orderService;
    private final OrderDetailsService orderDetailsService;
    private final InvoiceService invoiceService;
    private final CartService cartService;

    @Transactional(rollbackFor = Exception.class)
    @Override
    public InvoiceResponse checkoutCart(CheckoutRequest request) {
        Long totalPayment = 0L;

        // Get current authenticated customer
        UserAccount userAccount = (UserAccount) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Customer customer = userAccount.getCustomer();

        // Create invoice
        Invoice invoice = Invoice.builder()
                .paymentMethod(PaymentMethod.getAndValidatePaymentMethod(request.getPaymentMethod()))
                .customer(customer)
                .build();

        invoice = invoiceService.createInvoice(invoice);

        // Create order
        for (OrderRequest orderRequest : request.getOrders()) {
            Order order = Order.builder()
                    .shippingProvider(ShippingProvider.getAndValidateShippingProvider(orderRequest.getShippingProvider()))
                    .invoice(invoice)
                    .build();

            order = orderService.createOrder(order);

            Store checkStore = null;
            // Create and add all the products in order details
            for (OrderDetailsRequest orderDetailsRequest : orderRequest.getOrderDetails()) {
                Cart cart = cartService.getOne(orderDetailsRequest.getCartId());

                OrderDetails orderDetails = OrderDetails.builder()
                        .product(cart.getProduct())
                        .price(cart.getProduct().getPrice())
                        .quantity(cart.getQuantity())
                        .order(order)
                        .build();

                if (checkStore == null) {
                    checkStore = cart.getProduct().getStore();
                } else {
                    if (!checkStore.equals(cart.getProduct().getStore())) {
                        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Cannot add product with different store in one order");
                    }
                }

                orderDetailsService.createOrderDetails(orderDetails);

                totalPayment += (long) cart.getProduct().getPrice() * cart.getQuantity();
                cartService.deleteCart(cart);
            }
        }

        invoice.setTotalPayment(totalPayment);
        invoice = invoiceService.createInvoice(invoice);

        return InvoiceResponse.builder()
                .id(invoice.getId())
                .totalPayment(invoice.getTotalPayment())
                .paymentDueDate(invoice.getPaymentDueDate())
                .paymentCode(invoice.getPaymentCode())
                .paymentMethod(invoice.getPaymentMethod())
                .status(invoice.getPaymentStatus())
                .build();
    }
}
