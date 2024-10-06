package io.github.muhammadredin.tokonyadiaapi.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class SearchProductRequest extends PagingAndSortingRequest {
    private String query;
    private Integer minPrice;
    private Integer maxPrice;
}
