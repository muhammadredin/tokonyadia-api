package io.github.muhammadredin.tokonyadiaapi.dto.request;

import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TransactionRequest {
    private List<String> carts;
}
