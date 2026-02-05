package com.bookripple.api.domain.order.converter;

import com.bookripple.api.domain.order.dto.TradeReqDto;
import com.bookripple.api.domain.order.entity.Payment;
import com.bookripple.api.domain.order.entity.Settlement;
import com.bookripple.api.domain.order.entity.Trade;
import com.bookripple.api.domain.order.enums.PaymentStatus;
import com.bookripple.api.domain.order.enums.SettlementStatus;
import java.util.UUID;

public class TradeConverter {

    /**
     * [4단계] 결제(Payment) READY 상태 객체 생성
     */
    public static Payment toPayment(Trade trade, TradeReqDto.PreparePayment dto) {
        return Payment.builder()
                .trade(trade)
                .provider(dto.provider())
                .paymentKey("ORDER_" + UUID.randomUUID()) // 임시 키 생성
                .status(PaymentStatus.READY)
                .amount(trade.getAmount())
                .build();
    }

    /**
     * [4단계] 정산(Settlement) PENDING 상태 객체 생성
     */
    public static Settlement toSettlement(Trade trade) {
        return Settlement.builder()
                .trade(trade)
                .buyer(trade.getBuyer())
                .seller(trade.getSeller())
                .amount(trade.getAmount())
                .currency("KRW") // 사용자 엔티티 기준
                .status(SettlementStatus.PENDING)
                .build();
    }
}