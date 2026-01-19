package com.bookripple.api.domain.order.enums;

public enum PaymentStatus {
    READY,           // 결제 요청 (프론트에서 결제창을 띄우기 전 임시 저장)
    DONE,            // 결제 승인 완료 (돈이 빠져나간 상태)
    CANCELED,        // 결제 전체 취소
    ABORTED          // 결제 실패 (잔액 부족, 네트워크 오류 등으로 승인 실패)
}