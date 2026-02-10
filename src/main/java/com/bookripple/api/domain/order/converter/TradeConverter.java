package com.bookripple.api.domain.order.converter;

import com.bookripple.api.domain.order.dto.TradeReqDto;
import com.bookripple.api.domain.order.dto.TradeResDto;
import com.bookripple.api.domain.order.entity.Payment;
import com.bookripple.api.domain.order.entity.Settlement;
import com.bookripple.api.domain.order.entity.ShippingInfo;
import com.bookripple.api.domain.order.entity.Trade;
import com.bookripple.api.domain.order.enums.PaymentStatus;
import com.bookripple.api.domain.order.enums.SettlementStatus;


import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

public class TradeConverter {

    /**
     * [4단계] 결제(Payment) READY 상태 객체 생성
     */
    public static Payment toPayment(Trade trade, TradeReqDto.PreparePayment dto, String orderId) {
        return Payment.builder()
                .trade(trade)
                .provider(dto.provider())
                .paymentKey(null)
                .orderId(orderId) // 고유 주문 ID 생성
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

    /**
     * [4-1단계] 토스 응답 데이터를 바탕으로 Payment 엔티티 업데이트
     */
    public static void updatePaymentSuccess(Payment payment, String paymentKey) {
        payment.updatePaymentSuccess(
                paymentKey,
                PaymentStatus.DONE,
                LocalDateTime.now()
        );
    }

    /**
     * [5단계] 배송 정보(ShippingInfo) 엔티티 생성
     */
    public static ShippingInfo toShippingInfo(Trade trade, TradeReqDto.StartShipping dto) {
        return ShippingInfo.builder()
                .trade(trade)
                .companyName(dto.companyName())
                .shippingNumber(dto.shippingNumber())
                .build();
    }

    /**
     * [5단계] 판매자용 배송 정보 조회 DTO 변환
     */
    public static TradeResDto.SellerTradeDetail toSellerTradeDetail(Trade trade) {
        return TradeResDto.SellerTradeDetail.builder()
                .title(trade.getBlindSalePost().getSubtitle())
                .price(trade.getAmount())
                .buyerNickname(trade.getBuyer().getName())
                .shippingAddress(trade.getShippingAddress())
                .build();
    }
}