package com.bookripple.api.domain.toss.client;

import com.bookripple.api.domain.toss.config.TossPaymentConfig;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
@RequiredArgsConstructor
public class TossPaymentClient {

    private final RestClient tossRestClient; // TossPaymentConfig에서 만든 빈 주입

    /**
     * 토스 결제 승인 요청 (Confirm API)
     */
    public String confirmPayment(String paymentKey, String orderId, Integer amount) {
        // 실제 토스 API를 호출하는 로직이 들어갈 자리입니다.
        return "API 호출 준비 완료";
    }
}