package com.bookripple.api.domain.recommendation.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;

public class RecommendReqDto {

  @Builder
  public record Create(
      @Min(1)
      Long targetBookAladinId,
      @NotBlank
      String content
  ) {

  }
}
