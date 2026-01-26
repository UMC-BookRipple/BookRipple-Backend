package com.bookripple.api.domain.blindsalepost.entity;

import com.bookripple.api.domain.blindsalepost.enums.PurchaseStatus;
import com.bookripple.api.domain.member.entity.Member;
import com.bookripple.api.global.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "purchase_request")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public class PurchaseRequest extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 어떤 블라인드 게시글에 대한 요청인가? (N:1)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "blind_sale_post_id", nullable = false)
    private BlindSalePost blindSalePost;

    // 누가 구매를 요청했는가? (N:1)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    // 현재 요청의 상태 (기본값: WAITING)
    @Builder.Default
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PurchaseStatus status = PurchaseStatus.WAITING;

    // 판매자가 이 요청을 승인하는 메서드 (비즈니스 로직)
    public void accept() {
        this.status = PurchaseStatus.ACCEPTED;
    }
}