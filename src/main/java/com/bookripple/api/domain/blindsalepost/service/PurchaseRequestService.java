package com.bookripple.api.domain.blindsalepost.service;


import com.bookripple.api.domain.blindsalepost.dto.PurchaseRequestResDto;

public interface PurchaseRequestService {

  PurchaseRequestResDto.Create createPurchaseRequest(Long memberId, Long blindSalePostId);

  PurchaseRequestResDto.Decision cancelPurchaseRequest(Long memberId, Long purchaseRequestId);

  PurchaseRequestResDto.Decision approvePurchaseRequest(Long memberId, Long purchaseRequestId);

  PurchaseRequestResDto.Decision rejectPurchaseRequest(Long memberId, Long purchaseRequestId);

  PurchaseRequestResDto.Decision startShipping(Long memberId, Long purchaseRequestId);

  PurchaseRequestResDto.Decision completeShipping(Long memberId, Long purchaseRequestId);
}