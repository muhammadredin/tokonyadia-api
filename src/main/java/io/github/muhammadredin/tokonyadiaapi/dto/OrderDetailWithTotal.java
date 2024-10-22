package io.github.muhammadredin.tokonyadiaapi.dto;

import io.github.muhammadredin.tokonyadiaapi.dto.response.ProductOrderResponse;
import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderDetailWithTotal {
    private List<ProductOrderResponse> items;
    private Long totalPrice;
}
