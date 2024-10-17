package io.github.muhammadredin.tokonyadiaapi.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.github.muhammadredin.tokonyadiaapi.constant.PaymentType;
import io.github.muhammadredin.tokonyadiaapi.constant.TransactionStatus;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class InvoiceResponse {
    private String id;
    private PaymentType paymentType;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime expiryDate;
    private Long grossAmount;
    private TransactionStatus transactionStatus;
}
