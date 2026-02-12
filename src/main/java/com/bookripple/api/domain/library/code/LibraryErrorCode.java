package com.bookripple.api.domain.library.code;

import com.bookripple.api.common.code.BaseErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum LibraryErrorCode implements BaseErrorCode {

  NO_LIBRARY_BOOK(HttpStatus.NOT_FOUND, "LIBRARY_404", "책장에 등록되지 않은 책입니다.");
  private final HttpStatus httpStatus;
  private final String code;
  private final String message;

}
