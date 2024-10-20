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
    @NotBlank(message = "Transaction detail should be not empty")
    @JsonProperty("transaction_details")
    private MTTransactionDetail transactionDetails;
    @NotBlank(message = "Transaction detail should be not empty")
    @JsonProperty("item_details")
    private List<MTItemDetail> itemDetails;
    @NotBlank(message = "Transaction detail should be not empty")
    @JsonProperty("customer_details")
    private MTCustomerDetail customerDetails;
}

