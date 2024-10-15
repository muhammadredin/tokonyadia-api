package io.github.muhammadredin.tokonyadiaapi.dto.request;

import io.github.muhammadredin.tokonyadiaapi.dto.response.OrderDetailResponse;
import lombok.*;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class StoreOrderDetailResponse extends OrderDetailResponse {
    private String customerId;
    private String customerName;
}
