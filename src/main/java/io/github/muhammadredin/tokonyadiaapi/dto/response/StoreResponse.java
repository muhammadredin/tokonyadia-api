package io.github.muhammadredin.tokonyadiaapi.dto.response;

import lombok.*;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class StoreResponse {
    private String id;
    private String noSiup;
    private String name;
    private String address;
    private String phoneNumber;
    private String userId;
    private FileResponse image;
}
