package io.github.muhammadredin.tokonyadiaapi.client;

import io.github.muhammadredin.tokonyadiaapi.config.FeignClientConfig;
import io.github.muhammadredin.tokonyadiaapi.dto.request.midtransRequest.PaymentRequest;
import io.github.muhammadredin.tokonyadiaapi.dto.response.midtransResponse.MidtransSnapResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient(name = "midtransApp", url = "${tokonyadia.api.midtrans-snap-payment-url}", configuration = FeignClientConfig.class)
public interface MidtransAppClient {
    @PostMapping(
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    MidtransSnapResponse createSnapTransaction(
            @RequestBody PaymentRequest request,
            @RequestHeader(name = HttpHeaders.AUTHORIZATION) String authorization
    );
}
