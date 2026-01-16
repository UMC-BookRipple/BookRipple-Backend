package com.bookripple.api.domain.review.dto;

import java.time.LocalDateTime;
import java.util.List;
import lombok.Builder;

public class ReviewResDto {

  @Builder
  public record Item(
      Long id,
      String nickname,
      String content,
      LocalDateTime updatedAt
  ) {

  }

  @Builder
  public record ReviewList(
      List<Item> reviewList,
      Boolean hasNext,
      Long lastId
  ) {

  }
}
