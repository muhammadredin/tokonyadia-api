package io.github.muhammadredin.tokonyadiaapi.dto.request;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class LoginRequest {
    private String credential;
    private String password;
}
