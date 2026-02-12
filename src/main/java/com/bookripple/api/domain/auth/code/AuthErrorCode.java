package com.bookripple.api.domain.auth.code;

import com.bookripple.api.common.code.BaseErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum AuthErrorCode implements BaseErrorCode {

  // 400 BAD_REQUEST: 인증 데이터 오류
  INVALID_VERIFICATION_CODE(HttpStatus.BAD_REQUEST, "AUTH_400_1", "인증 코드가 일치하지 않습니다."),
  EXPIRED_VERIFICATION_CODE(HttpStatus.BAD_REQUEST, "AUTH_400_2", "인증 코드가 만료되었습니다."),
  NOT_FOUND_VERIFICATION_CODE(HttpStatus.BAD_REQUEST, "AUTH_400_3", "인증 요청 내역이 존재하지 않습니다."),

  // 401 UNAUTHORIZED: 로그인/토큰 실패
  LOGIN_FAILED(HttpStatus.UNAUTHORIZED, "AUTH_401_1", "아이디 또는 비밀번호가 올바르지 않습니다."),
  INVALID_TOKEN(HttpStatus.UNAUTHORIZED, "AUTH_401_2", "유효하지 않은 토큰입니다."),
  EXPIRED_TOKEN(HttpStatus.UNAUTHORIZED, "AUTH_401_3", "만료된 토큰입니다."),

  // 403 FORBIDDEN: 권한/인증 상태 부족
  EMAIL_NOT_VERIFIED(HttpStatus.FORBIDDEN, "AUTH_403_1", "이메일 인증이 완료되지 않았습니다."),

  // 500 INTERNAL_SERVER_ERROR: 서버 측 인증 시스템 오류
  EMAIL_SEND_FAIL(HttpStatus.INTERNAL_SERVER_ERROR, "AUTH_500_1", "이메일 전송에 실패했습니다.");

  private final HttpStatus httpStatus;
  private final String code;
  private final String message;
}