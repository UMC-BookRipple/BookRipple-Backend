package com.bookripple.api.domain.order.dto;

import lombok.Builder;

public class TradeResDto {
    @Builder
    public record SellerTradeDetail(
            String title,            // 책 제목
            Integer price,           // 가격
            String buyerNickname,    // 구매자 닉네임
            String shippingAddress   // 구매자가 입력한 한 줄 주소
    ) {}

    public record PreparePaymentResponse(
            String orderId,
            Integer amount
    ) {}
}
