package io.github.muhammadredin.tokonyadiaapi.dto.request;

import io.github.muhammadredin.tokonyadiaapi.constant.ValidationErrorMessage;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PasswordResetRequest {
    @NotBlank(message = ValidationErrorMessage.TOKEN_EMPTY_ERROR)
    private String token;

    @NotBlank(message = ValidationErrorMessage.PASSWORD_EMPTY_ERROR)
    @Size(min = 8, message = ValidationErrorMessage.PASSWORD_LENGTH_ERROR)
    private String password;
}
