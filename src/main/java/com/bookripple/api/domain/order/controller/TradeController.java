package com.bookripple.api.domain.order.controller;

import com.bookripple.api.domain.order.dto.TradeReqDto;
import com.bookripple.api.domain.order.dto.TradeResDto;
import com.bookripple.api.domain.order.service.TradeService;
import com.bookripple.api.global.annotation.PreventDuplicate;
import com.bookripple.api.global.code.CommonSuccessCode;
import com.bookripple.api.global.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Trade", description = "거래(주문) 관련 API")
@RestController
@RequestMapping("/api/v1/trades")
@RequiredArgsConstructor
public class TradeController {

  private final TradeService tradeService;

  // [POST] /api/v1/trades/{tradeId}/prepare
  // 결제 전 배송지 확정 및 결제 객체 생성
  @Operation(summary = "결제 준비", description = "구매자가 배송지 정보를 작성하고 결제 준비를 완료합니다.")
  @PreventDuplicate
  @PostMapping("/{tradeId}/prepare")
  public ApiResponse<TradeResDto.PreparePaymentResponse> preparePayment(
      @AuthenticationPrincipal Long memberId,
      @PathVariable Long tradeId,
      @RequestBody TradeReqDto.PreparePayment requestDto) {

    TradeResDto.PreparePaymentResponse response = tradeService.preparePayment(memberId, tradeId,
        requestDto);
    return ApiResponse.onSuccess(CommonSuccessCode.OK, response);
  }

  /**
   * [4-1단계] 결제 승인: 토스 인증 성공 후 최종 결제 처리
   */
  @Operation(summary = "결제 승인", description = "구매자가 결제를 승인하고 최종 결제를 완료합니다.")
  @PreventDuplicate
  @PostMapping("/{tradeId}/confirm")
  public ApiResponse<String> confirmPayment(
      @AuthenticationPrincipal Long memberId,
      @PathVariable Long tradeId) {

    tradeService.confirmPayment(memberId, tradeId);
    return ApiResponse.onSuccess(CommonSuccessCode.OK, "결제가 최종 완료되었습니다.");
  }

  /**
   * [4-2단계] 결제 전 취소: 구매자가 결제 전 단계에서 취소 시 상태 복구
   */
  @Operation(summary = "결제 전 취소", description = "구매자가 결제 전에 거래를 취소합니다.")
  @PreventDuplicate
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
  @Operation(summary = "판매자용 배송 시작 화면 조회", description = "판매자가 구매자의 정보와 구매자의 배송지를 조회합니다.")
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
  @Operation(summary = "배송 정보 등록", description = "판매자가 배송 정보를 등록하고 거래 상태를 '배송 중'으로 변경합니다.")
  @PreventDuplicate
  @PatchMapping("/{tradeId}/shipping")
  public ApiResponse<String> submitShippingInfo(
      @AuthenticationPrincipal Long memberId,
      @PathVariable Long tradeId,
      @RequestBody TradeReqDto.StartShipping requestDto) {

    tradeService.startShipping(memberId, tradeId, requestDto);
    return ApiResponse.onSuccess(CommonSuccessCode.OK, "배송 정보가 성공적으로 등록되었습니다.");
  }
}
