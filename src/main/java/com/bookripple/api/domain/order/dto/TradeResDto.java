package com.bookripple.api.domain.order.dto;

import lombok.Builder;

public class TradeResDto {
    // 배송 화면 준비를 위한 정보
    @Builder
    public record ShippingView(
            String title,        // 게시글 제목
            Integer price,       // 가격
            String status,       // "판매요청 승인" 또는 "결제완료"
            String buyerName,    // 구매자 이름 (RecipientName)
            String fullAddress   // 전체 주소 (Line1 + Line2)
    ) {}

    // 판매자가 입력한 정보를 받을 그릇
    public record StartShippingReq(
            String shippingMethod, // 배송 방법
            String trackingNumber  // 송장 번호
    ) {}
}
