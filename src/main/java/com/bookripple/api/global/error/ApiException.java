package com.bookripple.api.global.error;


import com.bookripple.api.global.code.BaseErrorCode;
import java.util.Collections;
import java.util.Map;
import lombok.Getter;

@Getter
public class ApiException extends RuntimeException {

  private final BaseErrorCode errorCode;
  private final Map<String, String> fieldErrors;

  public ApiException(BaseErrorCode errorCode) {
    super(errorCode.getMessage());
    this.errorCode = errorCode;
    this.fieldErrors = null;
  }

  public ApiException(BaseErrorCode errorCode, String overrideMessage) {
    super(overrideMessage != null ? overrideMessage : errorCode.getMessage());
    this.errorCode = errorCode;
    this.fieldErrors = null;
  }

  public ApiException(BaseErrorCode errorCode, Map<String, String> fieldErrors) {
    super(errorCode.getMessage());
    this.errorCode = errorCode;
    this.fieldErrors = fieldErrors != null ? Collections.unmodifiableMap(fieldErrors) : null;
  }

  public ApiException(BaseErrorCode errorCode, String overrideMessage,
      Map<String, String> fieldErrors) {
    super(overrideMessage != null ? overrideMessage : errorCode.getMessage());
    this.errorCode = errorCode;
    this.fieldErrors = fieldErrors != null ? Collections.unmodifiableMap(fieldErrors) : null;
  }

}
