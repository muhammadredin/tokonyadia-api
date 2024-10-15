package io.github.muhammadredin.tokonyadiaapi.dto.request;

import io.github.muhammadredin.tokonyadiaapi.dto.response.PaymentOrderResponse;
import lombok.*;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PaymentDetailResponse {
    private Long totalPrice;
    private String paymentMethod;
    private List<PaymentOrderResponse> orders;
}
