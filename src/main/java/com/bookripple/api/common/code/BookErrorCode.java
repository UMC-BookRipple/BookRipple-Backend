package com.bookripple.api.common.code;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum BookErrorCode implements BaseErrorCode {

  NO_BOOK(HttpStatus.NOT_FOUND, "BOOK_404", "책을 찾을 수 없습니다.");
  private final HttpStatus httpStatus;
  private final String code;
  private final String message;

}
