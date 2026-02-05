package com.bookripple.api.domain.order.controller;

import com.bookripple.api.common.code.CommonSuccessCode;
import com.bookripple.api.common.response.ApiResponse;
import com.bookripple.api.domain.order.dto.TradeReqDto;
import com.bookripple.api.domain.order.dto.TradeResDto;
import com.bookripple.api.domain.order.service.TradeService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/trades")
@RequiredArgsConstructor
public class TradeController {

    private final TradeService tradeService;

    // [POST] /api/v1/trades/{tradeId}/prepare
    // 결제 전 배송지 확정 및 결제 객체 생성
    @PostMapping("/{tradeId}/prepare")
    public ApiResponse<String> preparePayment(
            @AuthenticationPrincipal Long memberId,
            @PathVariable Long tradeId,
            @RequestBody TradeReqDto.PreparePayment requestDto) {

        tradeService.preparePayment(memberId, tradeId, requestDto);
        return ApiResponse.onSuccess(CommonSuccessCode.OK, "결제 준비가 완료되었습니다.");
    }

    /**
     * [4-1단계] 결제 승인: 토스 인증 성공 후 최종 결제 처리
     */
    @PostMapping("/{tradeId}/confirm")
    public ApiResponse<String> confirmPayment(
            @AuthenticationPrincipal Long memberId,
            @PathVariable Long tradeId,
            @RequestParam String paymentKey,
            @RequestParam String orderId,
            @RequestParam Integer amount) {

        tradeService.confirmPayment(memberId, tradeId, paymentKey, orderId, amount);
        return ApiResponse.onSuccess(CommonSuccessCode.OK, "결제가 최종 완료되었습니다.");
    }

    /**
     * [4-2단계] 결제 전 취소: 구매자가 결제 전 단계에서 취소 시 상태 복구
     */
    @PatchMapping("/{tradeId}/cancel")
    public ApiResponse<String> cancelTradeBeforePayment(
            @AuthenticationPrincipal Long memberId,
            @PathVariable Long tradeId) {

        tradeService.cancelTradeBeforePayment(memberId, tradeId);
        return ApiResponse.onSuccess(CommonSuccessCode.OK, "구매 요청 결제가 취소되었습니다.");
    }

    /**
     * [5단계 - 조회] 판매자 배송 시작 화면 정보 조회
     */
    @GetMapping("/{tradeId}/shipping")
    public ApiResponse<TradeResDto.SellerTradeDetail> getShippingScreenInfo(
            @AuthenticationPrincipal Long memberId,
            @PathVariable Long tradeId) {

        return ApiResponse.onSuccess(
                CommonSuccessCode.OK,
                tradeService.getSellerTradeDetail(memberId, tradeId)
        );
    }

    /**
     * [5단계 - 제출] 배송 정보 등록 및 상태 변경
     */
    @PatchMapping("/{tradeId}/shipping")
    public ApiResponse<String> submitShippingInfo(
            @AuthenticationPrincipal Long memberId,
            @PathVariable Long tradeId,
            @RequestBody TradeReqDto.StartShipping requestDto) {

        tradeService.startShipping(memberId, tradeId, requestDto);
        return ApiResponse.onSuccess(CommonSuccessCode.OK, "배송 정보가 성공적으로 등록되었습니다.");
    }
}
