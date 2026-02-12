package com.bookripple.api.domain.member.exception;

import com.bookripple.api.global.code.CommonErrorCode;
import com.bookripple.api.global.error.ApiException;

/**
 * 이메일 중복 시 발생 - 회원가입 - 이메일 변경 - 소셜 로그인 연동 시
 */
public class DuplicateEmailException extends ApiException {

  public DuplicateEmailException() {
    super(CommonErrorCode.CONFLICT, "이미 사용 중인 이메일입니다.");
  }
}