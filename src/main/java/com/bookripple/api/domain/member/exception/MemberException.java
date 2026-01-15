package com.bookripple.api.domain.member.exception;

import com.bookripple.api.common.code.BaseErrorCode;
import com.bookripple.api.common.error.ApiException;

public class MemberException extends ApiException {

  public MemberException(BaseErrorCode code) {
    super(code);
  }
}
