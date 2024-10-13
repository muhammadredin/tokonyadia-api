package io.github.muhammadredin.tokonyadiaapi.dto.response;

import lombok.*;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class ProductResponse {
    private String id;
    private String name;
    private String description;
    private Integer price;
    private Integer stock;
    private String storeName;
}