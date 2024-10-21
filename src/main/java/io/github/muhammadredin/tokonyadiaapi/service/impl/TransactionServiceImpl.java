package io.github.muhammadredin.tokonyadiaapi.service.impl;

import io.github.muhammadredin.tokonyadiaapi.client.MidtransAppClient;
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
import io.github.muhammadredin.tokonyadiaapi.util.HashUtil;
import io.github.muhammadredin.tokonyadiaapi.util.ValidationUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class TransactionServiceImpl implements TransactionService {
    private final OrderService orderService;
    private final OrderDetailsService orderDetailsService;
    private final InvoiceService invoiceService;
    private final CartService cartService;
    private final ValidationUtil validationUtil;
    private final MidtransAppClient midtransAppClient;

    @Value("${tokonyadia.api.midtrans-server-key}")
    private String MIDTRANS_SERVER_KEY;

    @Transactional(rollbackFor = Exception.class)
    @Override
    public MidtransSnapResponse checkoutCart(CheckoutRequest request) {
        // Validate the checkout request
        validationUtil.validate(request);
        Long totalPayment = 0L;

        // Get the current authenticated customer
        UserAccount userAccount = (UserAccount) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Customer customer = userAccount.getCustomer();

        // Extract first and last name from the customer's name
        String firstName = customer.getName().split(" ")[0];
        String lastName = customer.getName().substring(firstName.length() + 1);

        // Create customer details for Midtrans
        MTCustomerDetail customerDetails = MTCustomerDetail.builder()
                .firstName(firstName)
                .lastName(lastName)
                .email(userAccount.getEmail())
                .phone(userAccount.getPhoneNumber())
                .build();

        // Create an invoice for the customer
        Invoice invoice = Invoice.builder()
                .customer(customer)
                .build();

        invoice = invoiceService.createInvoice(invoice);

        // Create item details and process each order
        List<MTItemDetail> itemDetails = new ArrayList<>();

        for (OrderRequest orderRequest : request.getOrders()) {
            // Create an order associated with the invoice
            Order order = Order.builder()
                    .shippingProvider(ShippingProvider.getAndValidateShippingProvider(orderRequest.getShippingProvider()))
                    .invoice(invoice)
                    .build();

            order = orderService.createOrder(order);

            Store checkStore = null;

            // Process order details
            for (String cartId : orderRequest.getOrderDetails()) {
                Cart cart = cartService.getOne(cartId);

                OrderDetails orderDetails = OrderDetails.builder()
                        .product(cart.getProduct())
                        .price(cart.getProduct().getPrice())
                        .quantity(cart.getQuantity())
                        .order(order)
                        .build();

                // Check if the order contains products from different stores
                if (checkStore == null) {
                    checkStore = cart.getProduct().getStore();
                } else {
                    if (!checkStore.equals(cart.getProduct().getStore())) {
                        log.warn("Multiple stores found in a single order.");
                        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, PaymentResponseMessage.ERROR_MULTIPLE_STORE_IN_ORDER);
                    }
                }

                orderDetailsService.createOrderDetails(orderDetails);

                // Add price to total payment
                totalPayment += (long) cart.getProduct().getPrice() * cart.getQuantity();

                // Create item detail for Midtrans request
                itemDetails.add(
                        MTItemDetail.builder()
                                .id(cart.getProduct().getId())
                                .name(cart.getProduct().getName())
                                .price(cart.getProduct().getPrice())
                                .quantity(cart.getQuantity())
                                .build()
                );

                // Delete the cart item after checkout
                cartService.deleteCart(cart);
            }
        }

        // Prepare transaction details
        MTTransactionDetail transactionDetails = MTTransactionDetail.builder()
                .orderId(invoice.getId())
                .grossAmount(totalPayment)
                .build();

        // Prepare payment request for Midtrans
        PaymentRequest paymentRequest = PaymentRequest.builder()
                .transactionDetails(transactionDetails)
                .customerDetails(customerDetails)
                .itemDetails(itemDetails)
                .build();

        log.info("Payment request prepared: {}", paymentRequest);

        // Charge payment using Midtrans service
        String headerValue = "Basic " + Base64.getEncoder().encodeToString(MIDTRANS_SERVER_KEY.getBytes(StandardCharsets.UTF_8));
        MidtransSnapResponse midtransResponse = midtransAppClient.createSnapTransaction(paymentRequest, headerValue);

        // Update the invoice with Midtrans token and amount
        invoice.setMidtransToken(midtransResponse.getToken());
        invoice.setGrossAmount(totalPayment);
        invoiceService.createInvoice(invoice);

        return midtransResponse;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void updateInvoiceStatus(MidtransNotification notification) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        String invoiceId = notification.getOrderId();
        String status = notification.getTransactionStatus();

        validateSignatureKey(notification.getOrderId(), notification.getStatusCode(), notification.getGrossAmount(), notification.getSignatureKey());

        // Update invoice status based on notification
        switch (status) {
            case "pending":
                Invoice pendingInvoice = invoiceService.setInvoiceStatus(invoiceId, status);
                pendingInvoice.setPaymentType(PaymentType.fromDescription(notification.getPaymentType()));
                pendingInvoice.setExpiryTime(LocalDateTime.parse(notification.getExpiryTime(), formatter));
                pendingInvoice.setTransactionId(notification.getTransactionId());
                pendingInvoice.setTransactionTime(LocalDateTime.parse(notification.getTransactionTime(), formatter));
                invoiceService.createInvoice(pendingInvoice);
                log.info("Invoice status updated to pending for invoice ID: {}", invoiceId);
                break;

            case "settlement":
                Invoice settlementInvoice = invoiceService.setInvoiceStatus(invoiceId, status);
                for (Order order : settlementInvoice.getOrder()) {
                    order.setOrderStatus(OrderStatus.VERIFIED);
                    orderService.updateOrderStatus(order);
                }
                settlementInvoice.setSettlementTime(LocalDateTime.parse(notification.getSettlementTime(), formatter));
                invoiceService.createInvoice(settlementInvoice);
                log.info("Invoice status updated to settlement for invoice ID: {}", invoiceId);
                break;

            case "expire":
                Invoice expiredInvoice = invoiceService.setInvoiceStatus(invoiceId, status);
                for (Order order : expiredInvoice.getOrder()) {
                    order.setOrderStatus(OrderStatus.CANCELLED);
                    orderService.updateOrderStatus(order);
                }
                log.info("Invoice status updated to expired for invoice ID: {}", invoiceId);
                break;

            default:
                log.warn("Received unrecognized transaction status: {} for invoice ID: {}", status, invoiceId);
                break;
        }
    }

    private void validateSignatureKey(String orderId, String statusCode, String grossAmount, String midtransSignatureKey) {
        String rawString = orderId + statusCode + grossAmount + MIDTRANS_SERVER_KEY;
        String signatureKey = HashUtil.encryptThisString(rawString);
        if (!signatureKey.equalsIgnoreCase(midtransSignatureKey)) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid signature key");
        }
    }
}
