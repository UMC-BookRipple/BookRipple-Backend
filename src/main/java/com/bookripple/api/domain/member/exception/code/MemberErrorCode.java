package com.bookripple.api.domain.member.exception.code;

import com.bookripple.api.common.code.BaseErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum MemberErrorCode implements BaseErrorCode {

  NO_MEMBER(HttpStatus.NOT_FOUND, "MEMBER_404", "멤버를 찾을 수 없습니다.");
  private final HttpStatus httpStatus;
  private final String code;
  private final String message;
}
