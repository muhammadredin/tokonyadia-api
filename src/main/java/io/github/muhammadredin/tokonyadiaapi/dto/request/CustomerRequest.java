package io.github.muhammadredin.tokonyadiaapi.dto.request;

import io.github.muhammadredin.tokonyadiaapi.constant.ValidationErrorMessage;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CustomerRequest {
    @NotBlank(message = ValidationErrorMessage.NAME_EMPTY_ERROR)
    @Size(min = 3, max = 50, message = ValidationErrorMessage.NAME_LENGTH_ERROR)
    private String name;

    @NotBlank(message = ValidationErrorMessage.ADDRESS_EMPTY_ERROR)
    private String address;
}
