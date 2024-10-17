package io.github.muhammadredin.tokonyadiaapi.dto.response.midtransResponse;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MidtransSnapResponse {
    private String token;
    @JsonProperty("redirect_url")
    private String redirectUrl;
}
