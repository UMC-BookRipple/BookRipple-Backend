package com.bookripple.api.domain.order.entity;

import com.bookripple.api.domain.order.enums.PaymentProvider;
import com.bookripple.api.domain.order.enums.PaymentStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "payment")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "trade_id", nullable = false)
    private Trade trade;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PaymentProvider provider;

    @Column(nullable = true, length = 200)
    private String paymentKey;

    @Column(name = "order_id", nullable = false, unique = true, length = 100)
    private String orderId;  // 추가

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PaymentStatus status;

    @Column(nullable = false)
    private Integer amount;

    @Column(name = "approved_at")
    private LocalDateTime approvedAt;

    @Column(name = "raw_response", columnDefinition = "TEXT")
    private String rawResponse;

    public void updatePaymentSuccess(String paymentKey, PaymentStatus status, LocalDateTime approvedAt) {
        this.paymentKey = paymentKey;
        this.status = status;
        this.approvedAt = approvedAt;
    }

}
