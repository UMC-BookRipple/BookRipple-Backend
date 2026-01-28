package com.bookripple.api.domain.order.enums;

public enum TradeStatus {
    REQUESTED, // 구매요청(결제 전)
    APPROVED, // 판매자 승인(결제 가능)
    REJECTED, // 구매자가 거래를 취소할 때
    PAID,
    SHIPPED,
    COMPLETED
}
