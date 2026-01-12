package com.bookripple.api.common.code;

import com.bookripple.api.common.code.BaseErrorCode;
import org.springframework.http.HttpStatus;

public enum CommonErrorCode implements BaseErrorCode {
    BAD_REQUEST(HttpStatus.BAD_REQUEST, "COMMON_400", "요청이 올바르지 않습니다."),
    UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "COMMON_401", "인증이 필요합니다."),
    FORBIDDEN(HttpStatus.FORBIDDEN, "COMMON_403", "접근 권한이 없습니다."),
    NOT_FOUND(HttpStatus.NOT_FOUND, "COMMON_404", "대상을 찾을 수 없습니다."),
    CONFLICT(HttpStatus.CONFLICT, "COMMON_409", "요청이 충돌했습니다."),
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "COMMON_500", "서버 오류가 발생했습니다.");

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;

    CommonErrorCode(HttpStatus httpStatus, String code, String message) {
        this.httpStatus = httpStatus;
        this.code = code;
        this.message = message;
    }

    @Override
    public HttpStatus getHttpStatus() {
        return httpStatus;
    }

    @Override
    public String getCode() {
        return code;
    }

    @Override
    public String getMessage() {
        return message;
    }
}