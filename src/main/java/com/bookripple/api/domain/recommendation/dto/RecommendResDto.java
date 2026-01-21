package com.bookripple.api.domain.recommendation.dto;

import java.time.LocalDateTime;
import java.util.List;
import lombok.Builder;

public class RecommendResDto {

  @Builder
  public record Recommend(
      Long id,
      String sourceBookTitle,
      Long targetBookId,
      String targetBookTitle,
      String targetBookCover,
      String targetBookAuthor,
      String nickname,
      String content,
      LocalDateTime updatedAt
  ) {

  }

  @Builder
  public record RecommendList(
      List<Recommend> recommendList,
      Boolean hasNext,
      Long lastId
  ) {

  }

}
