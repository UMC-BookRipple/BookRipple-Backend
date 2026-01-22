package com.bookripple.api.domain.question.dto;

import com.bookripple.api.global.enums.QuestionType;
import java.time.LocalDateTime;
import java.util.List;
import lombok.Builder;

public class QuestionResDto {

  @Builder
  public record Q(
      Long id,
      QuestionType type,
      String content,
      LocalDateTime createdAt
  ) {

  }

  @Builder
  public record QuestionList(
      List<Q> questionList,
      Boolean hasNext,
      long totalCnt,
      Long lastId
  ) {

  }

}
