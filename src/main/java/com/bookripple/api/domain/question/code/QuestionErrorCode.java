package com.bookripple.api.domain.question.code;

import com.bookripple.api.global.code.BaseErrorCode;
import org.springframework.http.HttpStatus;

public enum QuestionErrorCode implements BaseErrorCode {
  QUESTION_NOT_FOUND(HttpStatus.NOT_FOUND, "QUESTION_404", "질문을 찾을 수 없습니다."),
  QUESTION_FORBIDDEN(HttpStatus.FORBIDDEN, "QUESTION_403", "질문에 대한 권한이 없습니다."),
  INSUFFICIENT_PROGRESS(HttpStatus.FORBIDDEN, "QUESTION_403_2", "질문은 책을 30% 이상 읽어야 합니다."),
  INSUFFICIENT_PROGRESS_2(HttpStatus.FORBIDDEN, "QUESTION_403_3", "책을 완독해야합니다.");

  private final HttpStatus httpStatus;
  private final String code;
  private final String message;

  QuestionErrorCode(HttpStatus httpStatus, String code, String message) {
    this.httpStatus = httpStatus;
    this.code = code;
    this.message = message;
  }

  @Override
  public HttpStatus getHttpStatus() {
    return httpStatus;
  }

  @Override
  public String getCode() {
    return code;
  }

  @Override
  public String getMessage() {
    return message;
  }
}