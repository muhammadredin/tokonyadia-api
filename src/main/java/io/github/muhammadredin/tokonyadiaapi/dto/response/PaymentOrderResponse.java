package io.github.muhammadredin.tokonyadiaapi.dto.response;

import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentOrderResponse {
    private String storeName;
    private String shippingProvider;
    private List<ProductOrderResponse> products;
}
