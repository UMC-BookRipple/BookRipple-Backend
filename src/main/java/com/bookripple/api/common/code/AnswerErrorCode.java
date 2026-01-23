package com.bookripple.api.common.code;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum AnswerErrorCode implements BaseErrorCode {
  NO_ANSWER(HttpStatus.NOT_FOUND, "ANSWER_404", "답변을 찾을 수 없습니다."),
  FORBIDDEN(HttpStatus.FORBIDDEN, "ANSWER_403", "답변에 대한 권한이 없습니다.");

  private final HttpStatus httpStatus;
  private final String code;
  private final String message;
}
