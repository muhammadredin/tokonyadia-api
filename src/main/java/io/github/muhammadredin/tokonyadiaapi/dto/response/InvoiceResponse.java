package io.github.muhammadredin.tokonyadiaapi.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.github.muhammadredin.tokonyadiaapi.constant.PaymentMethod;
import io.github.muhammadredin.tokonyadiaapi.constant.PaymentStatus;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InvoiceResponse {
    private String id;
    // TODO: Nanti diganti dengan QR/Kode Pembayaran Dari Midtrans
    private String paymentCode;
    private PaymentMethod paymentMethod;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDateTime paymentDueDate;
    private Long totalPayment;
    private PaymentStatus status;
}
