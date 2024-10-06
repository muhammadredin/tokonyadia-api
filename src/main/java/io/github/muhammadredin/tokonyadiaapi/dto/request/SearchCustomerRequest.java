package io.github.muhammadredin.tokonyadiaapi.dto.request;

import lombok.*;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class SearchCustomerRequest extends PagingAndSortingRequest {
    private String query;
}
