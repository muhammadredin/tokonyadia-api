package io.github.muhammadredin.tokonyadiaapi.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import io.github.muhammadredin.tokonyadiaapi.dto.response.midtransResponse.MidtransSnapResponse;

public interface MidtransService {
    MidtransSnapResponse chargePayment(Object paymentRequest) throws JsonProcessingException;
}
