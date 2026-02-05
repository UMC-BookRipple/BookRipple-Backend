package com.bookripple.api.domain.order.service;

import com.bookripple.api.domain.order.dto.TradeReqDto;

public interface TradeService {
    void preparePayment(Long memberId, Long tradeId, TradeReqDto.PreparePayment dto);
    void confirmPayment(Long memberId, Long tradeId, String paymentKey, String orderId, Integer amount);
    void cancelTradeBeforePayment(Long memberId, Long tradeId);
    void startShipping(Long memberId, Long tradeId, TradeReqDto.StartShipping dto);
}
