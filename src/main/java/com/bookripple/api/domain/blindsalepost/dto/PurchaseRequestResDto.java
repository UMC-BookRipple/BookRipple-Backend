package com.bookripple.api.domain.blindsalepost.dto;

import com.bookripple.api.domain.blindsalepost.enums.PurchaseStatus;
import java.time.LocalDateTime;
import lombok.Builder;

public class PurchaseRequestResDto {
  @Builder
  public record Create(
      Long purchaseRequestId,
      Long blindBookId,
      PurchaseStatus status,
      LocalDateTime createdAt
  ){

  }

  @Builder
  public record Decision(
      Long purchaseRequestId,
      PurchaseStatus status,
      LocalDateTime updatedAt
  ){

  }
}
