package com.bookripple.api.domain.memo.code;

import com.bookripple.api.common.code.BaseErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum MemoErrorCode implements BaseErrorCode {
  NO_MEMO(HttpStatus.NOT_FOUND, "MEMO_404", "메모를 찾을 수 없습니다."),
  FORBIDDEN(HttpStatus.FORBIDDEN, "MEMO_403", "메모에 대한 권한이 없습니다.");

  private final HttpStatus httpStatus;
  private final String code;
  private final String message;
}


