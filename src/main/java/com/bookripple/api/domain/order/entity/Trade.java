package com.bookripple.api.domain.order.entity;

import com.bookripple.api.domain.blindsalepost.entity.BlindSalePost;
import com.bookripple.api.domain.member.entity.Member;
import com.bookripple.api.domain.order.enums.TradeStatus;
import com.bookripple.api.global.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "trade")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Trade extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "buyer_id", nullable = false)
    private Member buyer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "seller_id", nullable = false)
    private Member seller;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "blindsalepost_id", nullable = false)
    private BlindSalePost blindSalePost;

    @Column(nullable = false)
    private Integer amount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private TradeStatus status = TradeStatus.REQUESTED;

    // 수정사함 -> 모든 배송 정보를 한 줄의 텍스트로 받습니다.
    @Column(columnDefinition = "TEXT")
    private String shippingAddress;

    // 주소 입력 편의 메서드
    public void updateShippingAddress(String address) {
        this.shippingAddress = address;
    }

    public void updateStatus(TradeStatus status) {
        this.status = status;
    }

}
