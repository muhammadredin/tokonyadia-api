package io.github.muhammadredin.tokonyadiaapi.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class CustomerResponse {
    private String id;
    private String name;
    private String address;
    private FileResponse profileImage;
}
