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

  @Builder
  public record MyReview(
      Long id,
      String bookTitle,
      String content,
      LocalDateTime updatedAt
  ) {

  }

  @Builder
  public record MyReviewList(
      List<MyReview> myReviewList,
      Boolean hasNext,
      String lastBookTitle,
      Long lastId
  ) {

  }
}
