package io.github.muhammadredin.tokonyadiaapi.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import io.github.muhammadredin.tokonyadiaapi.constant.OrderStatus;
import io.github.muhammadredin.tokonyadiaapi.constant.PaymentResponseMessage;
import io.github.muhammadredin.tokonyadiaapi.constant.PaymentType;
import io.github.muhammadredin.tokonyadiaapi.constant.ShippingProvider;
import io.github.muhammadredin.tokonyadiaapi.dto.request.CheckoutRequest;
import io.github.muhammadredin.tokonyadiaapi.dto.request.OrderRequest;
import io.github.muhammadredin.tokonyadiaapi.dto.request.midtransRequest.*;
import io.github.muhammadredin.tokonyadiaapi.dto.response.midtransResponse.MidtransSnapResponse;
import io.github.muhammadredin.tokonyadiaapi.entity.*;
import io.github.muhammadredin.tokonyadiaapi.service.*;
import io.github.muhammadredin.tokonyadiaapi.util.ValidationUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class TransactionServiceImpl implements TransactionService {
    private final OrderService orderService;
    private final OrderDetailsService orderDetailsService;
    private final InvoiceService invoiceService;
    private final CartService cartService;
    private final MidtransService midtransService;
    private final ValidationUtil validationUtil;

    @Transactional(rollbackFor = Exception.class)
    @Override
    public MidtransSnapResponse checkoutCart(CheckoutRequest request) {
        validationUtil.validate(request);
        Long totalPayment = 0L;

        // Create Customer Detail

        // Get current authenticated customer
        UserAccount userAccount = (UserAccount) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Customer customer = userAccount.getCustomer();

        // Get Customer fName and lName
        String firstName = customer.getName().split(" ")[0];
        String lastName = customer.getName().substring(firstName.length() + 1);

        MTCustomerDetail customerDetails = MTCustomerDetail.builder()
                .firstName(firstName)
                .lastName(lastName)
                .email(userAccount.getEmail())
                .phone(userAccount.getPhoneNumber())
                .build();


        // Create invoice
        Invoice invoice = Invoice.builder()
                .customer(customer)
                .build();

        invoice = invoiceService.createInvoice(invoice);

        MTTransactionDetail transactionDetails = MTTransactionDetail.builder()
                .orderId(invoice.getId())
                .build();


        // Create Item Detail
        List<MTItemDetail> itemDetails = new ArrayList<>();

        // Create order
        for (OrderRequest orderRequest : request.getOrders()) {
            Order order = Order.builder()
                    .shippingProvider(ShippingProvider.getAndValidateShippingProvider(orderRequest.getShippingProvider()))
                    .invoice(invoice)
                    .build();

            order = orderService.createOrder(order);

            Store checkStore = null;

            // Create and add all the products in order details
            for (String cartId : orderRequest.getOrderDetails()) {
                Cart cart = cartService.getOne(cartId);

                OrderDetails orderDetails = OrderDetails.builder()
                        .product(cart.getProduct())
                        .price(cart.getProduct().getPrice())
                        .quantity(cart.getQuantity())
                        .order(order)
                        .build();

                // Cek apabila dalam satu order terdapat product dari store yang berbeda
                if (checkStore == null) {
                    checkStore = cart.getProduct().getStore();
                } else {
                    if (!checkStore.equals(cart.getProduct().getStore())) {
                        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, PaymentResponseMessage.ERROR_MULTIPLE_STORE_IN_ORDER);
                    }
                }

                orderDetailsService.createOrderDetails(orderDetails);

                // Tambahkan Harga ke Total Payment
                totalPayment += (long) cart.getProduct().getPrice() * cart.getQuantity();

                // Buat Item Detail untuk request Midtrans
                itemDetails.add(
                        MTItemDetail.builder()
                                .id(cart.getProduct().getId())
                                .name(cart.getProduct().getName())
                                .price(cart.getProduct().getPrice())
                                .quantity(cart.getQuantity())
                                .build()
                );

                // Hapus Cart yang sudah di Checkout
                cartService.deleteCart(cart);
            }
        }

        transactionDetails.setGrossAmount(totalPayment);

        // Midtrans request
        PaymentRequest paymentRequest = PaymentRequest.builder()
                .transactionDetails(transactionDetails)
                .customerDetails(customerDetails)
                .itemDetails(itemDetails)
                .build();
        log.info(paymentRequest.toString());

        try {
            MidtransSnapResponse midtransResponse = midtransService.chargePayment(paymentRequest);

            invoice.setMidtransToken(midtransResponse.getToken());
            invoice.setGrossAmount(totalPayment);
            invoiceService.createInvoice(invoice);

            return midtransResponse;
        } catch (JsonProcessingException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void updateInvoiceStatus(MidtransNotification notification) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        String invoiceId = notification.getOrderId();
        String status = notification.getTransactionStatus();

        if ("pending".equals(status)) {
            Invoice invoice = invoiceService.setInvoiceStatus(invoiceId, status);

            invoice.setPaymentType(PaymentType.fromDescription(notification.getPaymentType()));
            invoice.setExpiryTime(LocalDateTime.parse(notification.getExpiryTime(), formatter));
            invoice.setTransactionId(notification.getTransactionId());
            invoice.setTransactionTime(LocalDateTime.parse(notification.getTransactionTime(), formatter));
            invoiceService.createInvoice(invoice);
            return;
        }

        if ("settlement".equals(status)) {
            Invoice invoice = invoiceService.setInvoiceStatus(invoiceId, status);

            for (Order order : invoice.getOrder()) {
                order.setOrderStatus(OrderStatus.VERIFIED);
                orderService.updateOrderStatus(order);
            }
            invoice.setSettlementTime(LocalDateTime.parse(notification.getSettlementTime(), formatter));
            invoiceService.createInvoice(invoice);
            return;
        }

        if ("expire".equals(status)) {
            Invoice invoice = invoiceService.setInvoiceStatus(invoiceId, status);

            for (Order order : invoice.getOrder()) {
                order.setOrderStatus(OrderStatus.CANCELLED);
                orderService.updateOrderStatus(order);
            }
        }
    }
}
