package io.github.muhammadredin.tokonyadiaapi.dto.request.midtransRequest;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MTTransactionDetail {
    @JsonProperty("order_id")
    private String orderId;
    @JsonProperty("gross_amount")
    private Long grossAmount;
}
