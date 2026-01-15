package com.bookripple.api.domain.book.exception;

import com.bookripple.api.common.code.BaseErrorCode;
import com.bookripple.api.common.error.ApiException;

public class BookException extends ApiException {

  public BookException(BaseErrorCode code) {
    super(code);
  }
}
