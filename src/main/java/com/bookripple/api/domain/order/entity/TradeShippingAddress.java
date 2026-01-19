package com.bookripple.api.domain.order.entity;


import com.bookripple.api.global.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "trade_shipping_address")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
@Builder
public class TradeShippingAddress extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "trade_id", nullable = false)
    private Trade trade;

    @Column(nullable = false)
    private String recipientName;

    @Column(nullable = false,length = 20)
    private String phoneNumber;

    @Column(nullable = false)
    private String zipCode;

    @Column(nullable = false)
    private String addressLine1;

    @Column(nullable = false)
    private String addressLine2;

    @Column
    private String requestMessage;

}
