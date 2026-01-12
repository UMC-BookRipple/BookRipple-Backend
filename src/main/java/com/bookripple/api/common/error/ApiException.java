package com.bookripple.api.common.error;


import com.bookripple.api.common.code.BaseErrorCode;
import lombok.Getter;

import java.util.Collections;
import java.util.Map;

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

    public ApiException(BaseErrorCode errorCode, String overrideMessage, Map<String, String> fieldErrors) {
        super(overrideMessage != null ? overrideMessage : errorCode.getMessage());
        this.errorCode = errorCode;
        this.fieldErrors = fieldErrors != null ? Collections.unmodifiableMap(fieldErrors) : null;
    }

}
