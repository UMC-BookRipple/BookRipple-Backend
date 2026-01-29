package com.bookripple.api.global.dto;

import com.bookripple.api.global.validation.ValidationGroups;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import java.util.List;
import lombok.Builder;

public class GlobalDto {

  @Builder
  public record IdRes(
      Long id
  ) {

  }

  @Builder
  public record SingleRes<T>(T data) {

    public static <T> SingleRes<T> of(T data) {
      return SingleRes.<T>builder()
          .data(data)
          .build();
    }
  }

  public record ContentReq(
      @NotBlank
      @Size(max = 50, message = "질문은 50자를 넘을 수 없습니다.", groups = ValidationGroups.QuestionGroup.class)
      @Size(max = 200, message = "답변은 200자를 넘을 수 없습니다.", groups = ValidationGroups.AnswerGroup.class)
      @Size(max = 500, message = "감상평은 500자를 넘을 수 없습니다.", groups = ValidationGroups.ReviewGroup.class)
      @Size(max = 500, message = "추천글은 500자를 넘을 수 없습니다.", groups = ValidationGroups.RecommendGroup.class)
      @Size(max = 100, message = "메모는 100자를 넘을 수 없습니다.", groups = ValidationGroups.MemoGroup.class)
      String content
  ) {

  }

  public record IdList(
      @NotEmpty(message = "삭제할 ID는 최소 1개 이상이어야 합니다.")
      List<Long> idList
  ) {

  }
}
