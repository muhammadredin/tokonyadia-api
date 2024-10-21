package io.github.muhammadredin.tokonyadiaapi.dto.request.midtransRequest;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentRequest {
    @JsonProperty("transaction_details")
    private MTTransactionDetail transactionDetails;
    @JsonProperty("item_details")
    private List<MTItemDetail> itemDetails;
    @JsonProperty("customer_details")
    private MTCustomerDetail customerDetails;
}

