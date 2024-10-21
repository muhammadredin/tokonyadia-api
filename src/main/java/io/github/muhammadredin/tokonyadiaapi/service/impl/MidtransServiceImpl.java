package io.github.muhammadredin.tokonyadiaapi.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.muhammadredin.tokonyadiaapi.dto.request.midtransRequest.PaymentRequest;
import io.github.muhammadredin.tokonyadiaapi.dto.response.midtransResponse.MidtransSnapResponse;
import io.github.muhammadredin.tokonyadiaapi.service.MidtransService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;

import java.util.Collections;

@Service
@RequiredArgsConstructor
@Slf4j
public class MidtransServiceImpl implements MidtransService {
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    @Value("${tokonyadia.api.midtrans-snap-payment-url}")
    private String MIDTRANS_URL;

    @Transactional(rollbackFor = Exception.class)
    @Override
    public MidtransSnapResponse chargePayment(PaymentRequest paymentRequest) throws JsonProcessingException {
        // Convert the PaymentRequest object to JSON string
        String jsonBody = objectMapper.writeValueAsString(paymentRequest);
        log.info("Converted payment request to JSON: {}", jsonBody);

        // Set headers for the request
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        headers.set("Authorization", "Basic " + "U0ItTWlkLXNlcnZlci03SWxIS01faWQ0MXRpRzdmcE8teVNEQ046");

        // Create the HttpEntity to hold the request body and headers
        HttpEntity<String> requestEntity = new HttpEntity<>(jsonBody, headers);
        log.info("Request Entity created with headers: {}", requestEntity.getHeaders());

        // Send the POST request to the Midtrans API
        log.info("Sending payment request to Midtrans URL: {}", MIDTRANS_URL);
        ResponseEntity<String> response;
        try {
            response = restTemplate.exchange(
                    MIDTRANS_URL,
                    HttpMethod.POST,
                    requestEntity,
                    String.class
            );
            log.info("Received response from Midtrans: {}", response.getBody());
        } catch (Exception e) {
            log.error("Error occurred while calling Midtrans API: {}", e.getMessage());
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Payment processing failed");
        }

        // Convert the response body to MidtransSnapResponse object and return it
        return objectMapper.readValue(response.getBody(), MidtransSnapResponse.class);
    }
}
