package com.bookripple.api.domain.reading.code;

import com.bookripple.api.common.code.BaseErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ReadingErrorCode implements BaseErrorCode {
  NO_READING_SESSION(HttpStatus.NOT_FOUND, "READING_404", "독서 세션을 찾을 수 없습니다."),
  INVALID_READING_STATE(HttpStatus.BAD_REQUEST, "READING_400", "현재 독서 세션 상태에서는 이 작업을 수행할 수 없습니다."),
  ACTIVE_SESSION_ALREADY_EXISTS(HttpStatus.CONFLICT, "READING_409", "이미 진행 중인 독서 세션이 존재합니다."),

  INVALID_PAGE_RANGE(HttpStatus.BAD_REQUEST, "READING_400_PAGE_RANGE",
      "시작 페이지는 종료 페이지보다 클 수 없습니다."),
  INVALID_END_PAGE(HttpStatus.BAD_REQUEST, "READING_400_END_PAGE", "종료 페이지가 책의 전체 페이지 수를 초과했습니다.");


  private final org.springframework.http.HttpStatus httpStatus;
  private final String code;
  private final String message;
}
