package io.github.muhammadredin.tokonyadiaapi.controller;
import io.github.muhammadredin.tokonyadiaapi.constant.APIPath;
import io.github.muhammadredin.tokonyadiaapi.constant.OrderResponseMessage;
import io.github.muhammadredin.tokonyadiaapi.service.OrderService;
import io.github.muhammadredin.tokonyadiaapi.util.ResponseUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(APIPath.ORDER_API)
@RequiredArgsConstructor
public class OrderController {
    private final OrderService orderService;

    @GetMapping("/{id}")
    public ResponseEntity<?> getOrderById(
            @PathVariable String id
    ) {
        return ResponseUtil.buildResponse(HttpStatus.OK, OrderResponseMessage.SUCCESS_GET_ORDER_BY_ID, orderService.getCustomerOrderById(id));
    }
}
