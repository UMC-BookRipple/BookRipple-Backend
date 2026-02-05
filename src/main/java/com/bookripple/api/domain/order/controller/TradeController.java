package com.bookripple.api.domain.order.controller;

import com.bookripple.api.common.code.CommonSuccessCode;
import com.bookripple.api.common.response.ApiResponse;
import com.bookripple.api.domain.order.dto.TradeReqDto;
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
}
