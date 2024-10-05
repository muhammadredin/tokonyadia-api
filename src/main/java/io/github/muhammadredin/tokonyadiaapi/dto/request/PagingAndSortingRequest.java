package io.github.muhammadredin.tokonyadiaapi.dto.request;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PagingAndSortingRequest {
    private Integer page;
    private Integer size;
    private String sort;
}
