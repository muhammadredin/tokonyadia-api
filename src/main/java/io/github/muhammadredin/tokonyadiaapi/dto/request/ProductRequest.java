package io.github.muhammadredin.tokonyadiaapi.dto.request;

import lombok.*;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProductRequest {
    private String name;
    private String description;
    private Integer price;
    private Integer stock;
    private String storeId;
}
