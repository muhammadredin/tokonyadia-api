package io.github.muhammadredin.tokonyadiaapi.dto.response;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StoreOrderResponse {
    private String orderId;
    private String customerName;
    private String transactionStatus;
}
