package com.bookripple.api.domain.notification.enums;

public enum NotificationType {
    TRADE_REQUESTED,  // 구매 요청 옴 (판매자에게)
    TRADE_CANCELED,  // 구매 요청 취소 (판매자에게)
    TRADE_APPROVED,   // 거래 승인됨 (구매자에게) -> 결제 요청
    TRADE_REJECTED,   // 거래 거절됨 (구매자에게)
    PAYMENT_DONE,     // 결제 완료됨 (판매자에게) -> 배송 요청
    SHIPPING_STARTED, // 배송 시작됨 (구매자에게)
    SETTLEMENT_DONE,   // 거래 완료됨 (판매자에게)
    QUESTION_ANSWERED, // 질문에 답변
    READING_REMIND,    // 3일 이상 미독서
    REVIEW_REMIND      // 완독 후 감상평 미작성
}
