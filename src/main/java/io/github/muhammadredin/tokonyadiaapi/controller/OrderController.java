package io.github.muhammadredin.tokonyadiaapi.controller;
import io.github.muhammadredin.tokonyadiaapi.service.OrderService;
import io.github.muhammadredin.tokonyadiaapi.service.impl.CheckoutService;
import io.github.muhammadredin.tokonyadiaapi.util.ResponseUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/transaction")
@RequiredArgsConstructor
public class OrderController {
    private final OrderService orderService;

    @GetMapping("/{id}")
    public ResponseEntity<?> getTransactionById(
            @PathVariable String id
    ) {
        return ResponseUtil.buildResponse(HttpStatus.OK, "Transaction retrieved", orderService.getCustomerOrderById(id));
    }
}
