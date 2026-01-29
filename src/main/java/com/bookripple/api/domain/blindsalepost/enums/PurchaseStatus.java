package com.bookripple.api.domain.blindsalepost.enums;

public enum PurchaseStatus {
    WAITING,    // 판매 요청 중 (판매자가 수락하기 전)
    ACCEPTED,   // 판매 요청 승인 (결제 대기 중)
    REJECTED,   // 요청 거절
    PAYMENT_COMPLETED, // 결제 완료 (배송 대기)
    SHIPPING,   // 배송 중
    SHIPPED,    // 배송 완료
    CANCELLED   // 요청 취소
}