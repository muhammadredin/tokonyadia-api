package io.github.muhammadredin.tokonyadiaapi.dto.request;

import io.github.muhammadredin.tokonyadiaapi.constant.ValidationErrorMessage;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.*;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProductRequest {
    @NotBlank(message = ValidationErrorMessage.NAME_EMPTY_ERROR)
    private String name;

    @NotBlank(message = ValidationErrorMessage.DESCRIPTION_EMPTY_ERROR)
    private String description;

    @NotBlank(message = ValidationErrorMessage.PRICE_EMPTY_ERROR)
    @Min(value = 1, message = ValidationErrorMessage.PRICE_VALUE_ERROR)
    private Integer price;

    @NotBlank(message = ValidationErrorMessage.STOCK_EMPTY_ERROR)
    @Min(value = 0, message = ValidationErrorMessage.STOCK_VALUE_ERROR)
    private Integer stock;

    @NotBlank(message = ValidationErrorMessage.STORE_ID_EMPTY_ERROR)
    private String storeId;
}
