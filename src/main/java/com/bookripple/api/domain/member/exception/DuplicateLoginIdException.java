package com.bookripple.api.domain.member.exception;

import com.bookripple.api.global.code.CommonErrorCode;
import com.bookripple.api.global.error.ApiException;

/**
 * 로그인 아이디 중복 시 발생 - 회원가입 - 아이디 변경
 */
public class DuplicateLoginIdException extends ApiException {

  public DuplicateLoginIdException() {
    super(CommonErrorCode.CONFLICT, "이미 사용 중인 아이디입니다.");
  }
}