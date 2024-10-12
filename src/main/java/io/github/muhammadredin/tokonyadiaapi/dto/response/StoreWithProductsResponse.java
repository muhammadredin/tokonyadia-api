package io.github.muhammadredin.tokonyadiaapi.dto.response;

import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class StoreWithProductsResponse extends StoreResponse {
    private List<ProductResponse> products;
}