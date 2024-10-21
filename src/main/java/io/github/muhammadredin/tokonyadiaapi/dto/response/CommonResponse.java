package io.github.muhammadredin.tokonyadiaapi.dto.response;

import lombok.*;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Setter
public class CommonResponse<T> {
    private int status;
    private String message;
    private T data;
    private PagingResponse paging;
}
