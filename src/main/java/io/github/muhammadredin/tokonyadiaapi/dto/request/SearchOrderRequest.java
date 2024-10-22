package io.github.muhammadredin.tokonyadiaapi.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.*;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class SearchOrderRequest extends PagingAndSortingRequest {
    private String orderStatus;

    @NotBlank(message = "start date is required")
    private String startDate;

    @NotBlank(message = "end date is required")
    private String endDate;
}
