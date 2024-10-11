package io.github.muhammadredin.tokonyadiaapi.dto.request;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserAccountRequest {
    private String username;
    private String password;
    private String email;
    private String phoneNumber;
}
