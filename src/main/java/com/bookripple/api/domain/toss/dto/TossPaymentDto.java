package com.bookripple.api.domain.toss.dto;

import lombok.Builder;
import java.time.LocalDateTime;

public class TossPaymentDto {

    /**
     * [Request] 토스 결제 승인 요청 바디
     * 프론트엔드에서 넘겨받은 key와 id, 금액을 토스 서버로 보낼 때 사용합니다.
     */
    @Builder
    public record ConfirmRequest(
            String paymentKey,
            String orderId,
            Integer amount
    ) {}

    /**
     * [Response] 토스 결제 승인 응답
     * 토스 서버가 결제 완료 후 돌려주는 정보들입니다.
     */
    @Builder
    public record ConfirmResponse(
            String paymentKey,
            String orderId,
            String status,      // 결제 상태 (예: DONE)
            String approvedAt,  // 결제 승인 시점 (ISO 8601 형식)
            String method,      // 결제 수단 (카드, 가상계좌 등)
            Long totalAmount,   // 실제 결제된 총 금액
            String rawJson      // (선택) 전체 응답값을 저장하고 싶을 때 사용
    ) {}
}