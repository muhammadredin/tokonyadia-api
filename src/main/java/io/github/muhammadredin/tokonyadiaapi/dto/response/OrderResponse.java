package io.github.muhammadredin.tokonyadiaapi.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.github.muhammadredin.tokonyadiaapi.constant.OrderStatus;
import io.github.muhammadredin.tokonyadiaapi.constant.ShippingProvider;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderResponse {
    private String orderId;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDateTime orderDate;
    private OrderStatus orderStatus;
    private Integer totalPrice;
    private ShippingProvider shippingProvider;
    private List<ProductOrderResponse> productDetails;
}
