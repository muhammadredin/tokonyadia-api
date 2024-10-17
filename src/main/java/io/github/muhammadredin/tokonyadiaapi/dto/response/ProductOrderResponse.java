package io.github.muhammadredin.tokonyadiaapi.dto.response;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductOrderResponse {
    private String productId;
    private String productName;
    private Long productPrice;
    private Integer quantity;
}
