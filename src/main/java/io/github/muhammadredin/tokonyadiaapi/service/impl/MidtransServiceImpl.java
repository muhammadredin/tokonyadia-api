package io.github.muhammadredin.tokonyadiaapi.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.muhammadredin.tokonyadiaapi.dto.response.midtransResponse.MidtransSnapResponse;
import io.github.muhammadredin.tokonyadiaapi.service.MidtransService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;

import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class MidtransServiceImpl implements MidtransService {
    private final RestTemplate restTemplate;

    private final ObjectMapper objectMapper;

    @Value("${tokonyadia.api.midtrans-snap-payment-url}")
    private String MIDTRANS_URL;

    @Override
    public MidtransSnapResponse chargePayment(Object paymentRequest) throws JsonProcessingException {
        // Convert the map to JSON using ObjectMapper
        String jsonBody = objectMapper.writeValueAsString(paymentRequest);

        // Set headers
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        headers.set("Authorization", "Basic " + "U0ItTWlkLXNlcnZlci03SWxIS01faWQ0MXRpRzdmcE8teVNEQ046");

        // Create the HttpEntity
        HttpEntity<String> requestEntity = new HttpEntity<>(jsonBody, headers);

        log.info(requestEntity.getHeaders().toString());
        log.info(requestEntity.getBody());

        // Send the POST request
        ResponseEntity<String> response = restTemplate.exchange(
                MIDTRANS_URL,
                HttpMethod.POST,
                requestEntity,
                String.class
        );
        // Get the response
        return objectMapper.readValue(response.getBody(), MidtransSnapResponse.class);
    }
}
