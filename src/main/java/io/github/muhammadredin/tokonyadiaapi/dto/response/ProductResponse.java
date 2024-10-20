package io.github.muhammadredin.tokonyadiaapi.dto.response;

import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.List;

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
    private List<FileResponse> images;
}