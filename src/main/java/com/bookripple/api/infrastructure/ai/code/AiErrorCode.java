package com.bookripple.api.infrastructure.ai.code;

import com.bookripple.api.global.code.BaseErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum AiErrorCode implements BaseErrorCode {

  AI_BAD_GATEWAY(HttpStatus.BAD_GATEWAY, "AI_502", "응답 생성 중 오류가 발생했습니다."),
  AI_UNAVAILABLE(HttpStatus.SERVICE_UNAVAILABLE, "AI_503", "응답 처리 중 오류가 발생 했습니다.");
  private final HttpStatus httpStatus;
  private final String code;
  private final String message;
}
