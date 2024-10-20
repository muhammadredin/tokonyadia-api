package io.github.muhammadredin.tokonyadiaapi.dto.response;

import lombok.*;
import org.springframework.core.io.Resource;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FileDownloadResponse {
    private Resource resource;
    private String contentType;
}
