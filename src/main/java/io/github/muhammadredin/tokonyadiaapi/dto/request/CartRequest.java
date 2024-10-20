package io.github.muhammadredin.tokonyadiaapi.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CartRequest {
    @NotBlank(message = "Product ID should not be empty")
    private String productId;
    @NotNull(message = "Quantity should not be empty")
    @Min(value = 1, message = "Quantity should be greater than zero")
    private Integer quantity;
}
