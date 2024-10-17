package io.github.muhammadredin.tokonyadiaapi.controller;

import io.github.muhammadredin.tokonyadiaapi.dto.request.midtransRequest.MidtransNotification;
import io.github.muhammadredin.tokonyadiaapi.service.TransactionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/webhook/midtrans")
@Slf4j
@RequiredArgsConstructor
public class MidtransWebhookController {
    private final TransactionService transactionService;

    @PostMapping
    public ResponseEntity<?> handleMidtransWebhook(@RequestBody MidtransNotification notification) {
        log.info("Received MidtransNotification: {}", notification.toString());
        transactionService.updateInvoiceStatus(notification);
        return ResponseEntity.ok().body(notification);
    }
}
