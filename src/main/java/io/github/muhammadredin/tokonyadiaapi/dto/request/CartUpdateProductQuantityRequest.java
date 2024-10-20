package io.github.muhammadredin.tokonyadiaapi.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CartUpdateProductQuantityRequest {
    @NotNull(message = "Quantity should not be empty")
    @Min(value = 1, message = "Quantity should be greater than zero")
    private Integer quantity;
}
