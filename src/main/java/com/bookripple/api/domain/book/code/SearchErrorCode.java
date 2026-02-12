package com.bookripple.api.domain.book.code;

import com.bookripple.api.common.code.BaseErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum SearchErrorCode implements BaseErrorCode {
  NO_SEARCH_KEYWORD(null, "SEARCH_400", "검색어를 입력하세요.");


  private final org.springframework.http.HttpStatus httpStatus;
  private final String code;
  private final String message;
}
