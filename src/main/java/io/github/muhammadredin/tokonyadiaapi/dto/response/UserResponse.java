package io.github.muhammadredin.tokonyadiaapi.dto.response;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserResponse {
    private String id;
    private String username;
    private String email;
    private String phoneNumber;
    private String role;
}
