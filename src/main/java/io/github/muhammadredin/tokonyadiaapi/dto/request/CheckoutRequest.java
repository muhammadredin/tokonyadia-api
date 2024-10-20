package io.github.muhammadredin.tokonyadiaapi.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CheckoutRequest {
    @NotBlank(message = "Orders should not be empty")
    private List<OrderRequest> orders;
}
