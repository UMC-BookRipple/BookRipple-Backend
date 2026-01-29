package com.bookripple.api.domain.question.dto;

import com.bookripple.api.domain.question.enums.QuestionType;
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

  @Builder
  public record MyQ(
      Long id,
      String bookTitle,
      QuestionType type,
      String content,
      LocalDateTime createdAt
  ) {

  }

  @Builder
  public record MyQuestionList(
      List<MyQ> questionList,
      Boolean hasNext,
      String lastBookTitle,
      Long lastId
  ) {

  }

  @Builder
  public record ReadingAiQnA(
      Long id,
      QuestionType type,
      String question,
      String answer,
      LocalDateTime updatedAt
  ) {

  }

  @Builder
  public record ReadingAiQnAList(
      List<ReadingAiQnA> readingAiQnAS,
      Boolean hasNext,
      Long lastId
  ) {

  }

  @Builder
  public record History(
      String keyword,
      LocalDateTime createdAt
  ){

  }

  @Builder
  public record HistoryList(
      List<History> historyList
  ){

  }

}
