package io.github.muhammadredin.tokonyadiaapi.dto.request;

import io.github.muhammadredin.tokonyadiaapi.constant.ValidationErrorMessage;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ForgotPasswordRequest {
    @NotBlank(message = ValidationErrorMessage.EMAIL_EMPTY_ERROR)
    @Email(message = ValidationErrorMessage.EMAIL_NOT_VALID_ERROR)
    private String email;
}
