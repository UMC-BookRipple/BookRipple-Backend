package com.bookripple.api.domain.question.dto;

import java.time.LocalDateTime;
import java.util.List;
import lombok.Builder;

public class AnswerResDto {

  @Builder
  public record Ans(
      Long id,
      String content,
      LocalDateTime updatedAt,
      Boolean isMine
  ) {

  }

  @Builder
  public record AnswerList(
      List<Ans> ansList
  ) {

  }
}
