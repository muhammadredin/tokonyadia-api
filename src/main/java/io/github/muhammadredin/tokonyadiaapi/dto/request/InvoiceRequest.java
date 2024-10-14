package io.github.muhammadredin.tokonyadiaapi.dto.request;

import io.github.muhammadredin.tokonyadiaapi.constant.PaymentMethod;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class InvoiceRequest {
    private PaymentMethod paymentMethod;
}
