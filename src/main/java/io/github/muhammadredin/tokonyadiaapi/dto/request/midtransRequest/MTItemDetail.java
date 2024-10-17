package io.github.muhammadredin.tokonyadiaapi.dto.request.midtransRequest;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MTItemDetail {
    private String id;
    private String name;
    private Long price;
    private Integer quantity;
}
