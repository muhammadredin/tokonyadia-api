package io.github.muhammadredin.tokonyadiaapi.dto.response;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PagingResponse {
    private Long totalItems;
    private Integer totalPages;
    private Integer page;
    private Integer size;
}
