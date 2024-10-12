package io.github.muhammadredin.tokonyadiaapi.dto.request;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CustomerUpdateRequest {
    private String name;
    private String address ;
}
