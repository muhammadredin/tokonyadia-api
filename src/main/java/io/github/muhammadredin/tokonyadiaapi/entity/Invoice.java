package io.github.muhammadredin.tokonyadiaapi.entity;

import io.github.muhammadredin.tokonyadiaapi.constant.PaymentStatus;
import io.github.muhammadredin.tokonyadiaapi.constant.PaymentMethod;
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
@Table(name = "t_invoice")
public class Invoice {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Enumerated(EnumType.STRING)
    @Column(name = "payment_method", nullable = false)
    private PaymentMethod paymentMethod;

    @Enumerated(EnumType.STRING)
    @Column(name = "payment_status", nullable = false)
    private PaymentStatus paymentStatus;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "payment_due_date", nullable = false)
    private LocalDateTime paymentDueDate;

    @Column(name = "total_payment")
    private Long totalPayment;

    // TODO: Nanti diganti dengan payment code dari midtrans
    @Column(name = "payment_code")
    private String paymentCode;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id")
    private Customer customer;

    @OneToMany(mappedBy = "invoice")
    private List<Order> order;

    @PrePersist
    protected void onCreate() {
        paymentDueDate = LocalDateTime.now().plusDays(1);
        paymentStatus = PaymentStatus.PENDING;
        paymentCode = UUID.randomUUID().toString();
    }
}
