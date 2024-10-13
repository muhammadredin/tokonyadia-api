package io.github.muhammadredin.tokonyadiaapi.dto.request;

import io.github.muhammadredin.tokonyadiaapi.constant.ValidationErrorMessage;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class StoreRequest {
    @NotBlank(message = ValidationErrorMessage.NO_SIUP_EMPTY_ERROR)
    private String noSiup;

    @NotBlank(message = ValidationErrorMessage.NAME_EMPTY_ERROR)
    @Size(min = 3, max = 20, message = ValidationErrorMessage.STORE_NAME_LENGTH_ERROR)
    private String name;

    @NotBlank(message = ValidationErrorMessage.ADDRESS_EMPTY_ERROR)
    private String address;

    @NotBlank(message = ValidationErrorMessage.PHONE_NUMBER_EMPTY_ERROR)
    @Size(min = 10, max = 14, message = ValidationErrorMessage.PHONE_NUMBER_LENGTH_ERROR)
    @Pattern(regexp = "^[0-9]*$", message = ValidationErrorMessage.PHONE_NUMBER_NOT_VALID_ERROR)
    private String phoneNumber;
}
