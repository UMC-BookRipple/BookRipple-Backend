package com.bookripple.api.domain.order.dto;

import com.bookripple.api.domain.order.enums.PaymentProvider;
import lombok.Builder;

public class TradeReqDto {
    @Builder
    public record PreparePayment(
            String address,          // 주소지 한 줄
            PaymentProvider provider // TOSS 또는 MOCK
    ) {}
}
