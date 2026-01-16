package com.bookripple.api.domain.review.exception;

import com.bookripple.api.common.code.BaseErrorCode;
import com.bookripple.api.common.error.ApiException;

public class ReviewException extends ApiException {

  public ReviewException(BaseErrorCode code) {
    super(code);
  }
}
