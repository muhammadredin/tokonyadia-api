package io.github.muhammadredin.tokonyadiaapi.dto.response;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class StoreResponse {
    private String id;
    private String noSiup;
    private String name;
    private String address;
    private String phoneNumber;
}
