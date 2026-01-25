package com.bookripple.api.global.dto;

import com.bookripple.api.global.validation.ValidationGroups;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;

public class GlobalDto {

  @Builder
  public record IdRes(
      Long id
  ) {

  }


  public record ContentReq(
      @NotBlank
      @Size(max = 50, message = "질문은 50자를 넘을 수 없습니다.", groups = ValidationGroups.QuestionGroup.class)
      @Size(max = 200, message = "답변은 200자를 넘을 수 없습니다.", groups = ValidationGroups.AnswerGroup.class)
      String content
  ) {

  }
}
