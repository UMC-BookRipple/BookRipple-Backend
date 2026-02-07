package com.bookripple.api.domain.blindsalepost.controller;


import com.bookripple.api.common.code.CommonSuccessCode;
import com.bookripple.api.common.response.ApiResponse;
import com.bookripple.api.domain.blindsalepost.dto.PurchaseRequestResDto;
import com.bookripple.api.domain.blindsalepost.dto.PurchaseRequestResDto.Create;
import com.bookripple.api.domain.blindsalepost.service.PurchaseRequestService;
import com.bookripple.api.global.annotation.PreventDuplicate;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Validated
@RequestMapping("/api/v1/purchase-requests")
public class PurchaseRequestController {

  private final PurchaseRequestService purchaseRequestService;

  @PreventDuplicate
  @PostMapping("/{blindSalePostId}/create")
  public ApiResponse<Create> createPurchaseRequest(
      @AuthenticationPrincipal Long memberId,
      @PathVariable @Min(1) Long blindSalePostId
  ) {
    return ApiResponse.onSuccess(CommonSuccessCode.CREATED,
        purchaseRequestService.createPurchaseRequest(memberId, blindSalePostId));
  }

  @PreventDuplicate
  @PatchMapping("/{purchaseRequestId}/cancel")
  public ApiResponse<PurchaseRequestResDto.Decision> cancelPurchaseRequest(
      @AuthenticationPrincipal Long memberId,
      @PathVariable @Min(1) Long purchaseRequestId
  ) {
    return ApiResponse.onSuccess(CommonSuccessCode.OK,
        purchaseRequestService.cancelPurchaseRequest(memberId, purchaseRequestId));
  }

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
