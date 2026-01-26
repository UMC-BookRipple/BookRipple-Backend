package com.bookripple.api.domain.member.exception;

import com.bookripple.api.common.code.CommonErrorCode;
import com.bookripple.api.common.error.ApiException;

public class DuplicateEmailException extends ApiException {

  public DuplicateEmailException() {
    super(CommonErrorCode.CONFLICT, "이미 사용 중인 이메일입니다.");
  }
}