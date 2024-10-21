package io.github.muhammadredin.tokonyadiaapi.config;

import feign.Response;
import feign.codec.ErrorDecoder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

@Slf4j
public class FeignErrorDecoder implements ErrorDecoder {
    @Override
    public Exception decode(String s, Response response) {
        log.error("Error client: {}", response);
        HttpStatus status = HttpStatus.valueOf(response.status());

        return switch (status) {
            case UNAUTHORIZED -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Unauthorized");
            case NOT_FOUND -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Resource not found");
            case BAD_REQUEST -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Bad request");
            case INTERNAL_SERVER_ERROR ->
                    new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Internal server error");
            default -> new Exception("Generic error");
        };

    }
}
