package com.bookripple.api.domain.blindsalepost.converter;

import com.bookripple.api.domain.blindsalepost.dto.PurchaseRequestResDto;
import com.bookripple.api.domain.blindsalepost.entity.PurchaseRequest;

public class PurchaseRequestConverter {

  public static PurchaseRequestResDto.Create toCreate(PurchaseRequest purchaseRequest) {
    return PurchaseRequestResDto.Create.builder()
        .purchaseRequestId(purchaseRequest.getId())
        .blindBookId(purchaseRequest.getBlindSalePost().getBook().getId())
        .status(purchaseRequest.getStatus())
        .createdAt(purchaseRequest.getCreatedAt())
        .build();
  }

  public static PurchaseRequestResDto.Decision toDecision(PurchaseRequest purchaseRequest) {
    return PurchaseRequestResDto.Decision.builder()
        .purchaseRequestId(purchaseRequest.getId())
        .status(purchaseRequest.getStatus())
        .updatedAt(purchaseRequest.getUpdatedAt())
        .build();
  }

}
