package io.github.muhammadredin.tokonyadiaapi.dto.response;

import lombok.*;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class CartResponse extends ProductResponse {
    private String cartId;
    private Integer quantity;
}
