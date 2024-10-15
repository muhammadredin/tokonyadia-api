package io.github.muhammadredin.tokonyadiaapi.dto.request;

import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderRequest {
    private String shippingProvider;
    private List<String> orderDetails;
}
