package io.github.muhammadredin.tokonyadiaapi.dto.response;

import io.github.muhammadredin.tokonyadiaapi.constant.UserRole;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@Builder
public class LoginResponse {
    private String token;
    private String role;
}
