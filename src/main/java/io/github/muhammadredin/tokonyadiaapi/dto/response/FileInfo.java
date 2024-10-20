package io.github.muhammadredin.tokonyadiaapi.dto.response;

import lombok.*;

import java.nio.file.Path;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FileInfo {
    private String fileName;
    private Path filePath;
}
