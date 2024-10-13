package io.github.muhammadredin.tokonyadiaapi.dto.request;

import io.github.muhammadredin.tokonyadiaapi.constant.ValidationErrorMessage;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class LoginRequest {
    @NotBlank(message = ValidationErrorMessage.CREDENTIAL_EMPTY_ERROR)
    private String credential;

    @NotBlank(message = ValidationErrorMessage.PASSWORD_EMPTY_ERROR)
    @Size(min = 8, message = ValidationErrorMessage.PASSWORD_LENGTH_ERROR)
    private String password;
}
