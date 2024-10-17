package io.github.muhammadredin.tokonyadiaapi.dto.response;

import lombok.*;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class ProductResponse {
    private String productId;
    private String name;
    private String description;
    private Long price;
    private Integer stock;
    private String storeName;
}