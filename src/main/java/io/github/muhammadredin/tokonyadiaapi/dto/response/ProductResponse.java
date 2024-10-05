package io.github.muhammadredin.tokonyadiaapi.dto.response;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProductResponse {
    private String id;
    private String name;
    private String description;
    private Integer price;
    private Integer stock;
    private String storeName;
}