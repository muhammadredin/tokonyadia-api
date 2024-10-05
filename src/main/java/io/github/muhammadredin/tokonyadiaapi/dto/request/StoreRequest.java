package io.github.muhammadredin.tokonyadiaapi.dto.request;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class StoreRequest {
    private String noSiup;
    private String name;
    private String address;
    private String phoneNumber;
}
