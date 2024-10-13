package io.github.muhammadredin.tokonyadiaapi.dto.request;

import io.github.muhammadredin.tokonyadiaapi.constant.ValidationErrorMessage;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserAccountRequest {
    @NotBlank(message = ValidationErrorMessage.USERNAME_EMPTY_ERROR)
    @Size(min = 3, max = 20, message = ValidationErrorMessage.USERNAME_LENGTH_ERROR)
    private String username;

    @NotBlank(message = ValidationErrorMessage.PASSWORD_EMPTY_ERROR)
    @Size(min = 8, message = ValidationErrorMessage.PASSWORD_LENGTH_ERROR)
    private String password;

    @Email(message = ValidationErrorMessage.EMAIL_EMPTY_ERROR)
    private String email;

    @NotBlank(message = ValidationErrorMessage.PHONE_NUMBER_EMPTY_ERROR)
    @Size(min = 10, max = 14, message = ValidationErrorMessage.PHONE_NUMBER_LENGTH_ERROR)
    @Pattern(regexp = "^[0-9]*$", message = ValidationErrorMessage.PHONE_NUMBER_NOT_VALID_ERROR)
    private String phoneNumber;
}
