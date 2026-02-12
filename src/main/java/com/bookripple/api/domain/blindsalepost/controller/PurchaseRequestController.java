package com.bookripple.api.domain.blindsalepost.controller;


import com.bookripple.api.domain.blindsalepost.dto.PurchaseRequestResDto;
import com.bookripple.api.domain.blindsalepost.dto.PurchaseRequestResDto.Create;
import com.bookripple.api.domain.blindsalepost.service.PurchaseRequestService;
import com.bookripple.api.global.annotation.PreventDuplicate;
import com.bookripple.api.global.code.CommonSuccessCode;
import com.bookripple.api.global.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "PurchaseRequest", description = "구매 요청 관련 API")
@RestController
@RequiredArgsConstructor
@Validated
@RequestMapping("/api/v1/purchase-requests")
public class PurchaseRequestController {

  private final PurchaseRequestService purchaseRequestService;

  @Operation(summary = "구매 요청 생성", description = "블라인드 북 구매 요청을 생성합니다.")
  @PreventDuplicate
  @PostMapping("/{blindSalePostId}/create")
  public ApiResponse<Create> createPurchaseRequest(
      @AuthenticationPrincipal Long memberId,
      @PathVariable @Min(1) Long blindSalePostId
  ) {
    return ApiResponse.onSuccess(CommonSuccessCode.CREATED,
        purchaseRequestService.createPurchaseRequest(memberId, blindSalePostId));
  }

  @Operation(summary = "구매 요청 취소", description = "블라인드 북 구매 요청을 취소합니다.")
  @PreventDuplicate
  @PatchMapping("/{purchaseRequestId}/cancel")
  public ApiResponse<PurchaseRequestResDto.Decision> cancelPurchaseRequest(
      @AuthenticationPrincipal Long memberId,
      @PathVariable @Min(1) Long purchaseRequestId
  ) {
    return ApiResponse.onSuccess(CommonSuccessCode.OK,
        purchaseRequestService.cancelPurchaseRequest(memberId, purchaseRequestId));
  }

  @Operation(summary = "구매 요청 승인", description = "판매자가 블라인드 북 구매 요청을 승인합니다.")
  @PreventDuplicate
  @PatchMapping("/{purchaseRequestId}/approve")
  public ApiResponse<PurchaseRequestResDto.Decision> approvePurchaseRequest(
      @AuthenticationPrincipal Long memberId,
      @PathVariable @Min(1) Long purchaseRequestId
  ) {
    return ApiResponse.onSuccess(CommonSuccessCode.OK,
        purchaseRequestService.approvePurchaseRequest(memberId, purchaseRequestId));
  }


}
