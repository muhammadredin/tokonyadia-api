package io.github.muhammadredin.tokonyadiaapi.entity;

import io.github.muhammadredin.tokonyadiaapi.constant.PaymentType;
import io.github.muhammadredin.tokonyadiaapi.constant.TableName;
import io.github.muhammadredin.tokonyadiaapi.constant.TransactionStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = TableName.INVOICE_TABLE)
public class Invoice {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(name = "transaction_id")
    private String transactionId;

    @Column(name = "midtrans_token")
    private String midtransToken;

    @Enumerated(EnumType.STRING)
    @Column(name = "payment_type")
    private PaymentType paymentType;

    @Enumerated(EnumType.STRING)
    @Column(name = "transaction_status")
    private TransactionStatus transactionStatus;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "transaction_time")
    private LocalDateTime transactionTime;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "settlement_time")
    private LocalDateTime settlementTime;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "expiry_time")
    private LocalDateTime expiryTime;

    @Column(name = "gross_amount")
    private Long grossAmount;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id")
    private Customer customer;

    @OneToMany(mappedBy = "invoice", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private List<Order> order;

}
