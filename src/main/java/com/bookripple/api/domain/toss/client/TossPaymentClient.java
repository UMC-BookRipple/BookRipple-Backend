package com.bookripple.api.domain.toss.client;

import com.bookripple.api.domain.toss.config.TossPaymentConfig;
import com.bookripple.api.domain.toss.dto.TossPaymentDto;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
public class TossPaymentClient {

    private final RestClient tossRestClient;

    public TossPaymentClient(@Qualifier("tossRestClient") RestClient tossRestClient) {
        this.tossRestClient = tossRestClient;
    }


    /**
     * [4-1단계] 토스 결제 최종 승인 요청
     */
    public TossPaymentDto.ConfirmResponse confirmPayment(String paymentKey, String orderId, Integer amount) {
        return tossRestClient.post()
                .uri("/confirm")
                .body(TossPaymentDto.ConfirmRequest.builder()
                        .paymentKey(paymentKey)
                        .orderId(orderId)
                        .amount(amount)
                        .build())
                .retrieve()
                // 에러 발생 시 커스텀 예외 발생 (추후 수정 예정)
                .onStatus(HttpStatusCode::isError, (request, response) -> {
                    throw new RuntimeException("토스 결제 승인 실패");
                })
                .body(TossPaymentDto.ConfirmResponse.class); // DTO로 즉시 변환
    }
}