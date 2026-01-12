package com.bookripple.api.common.code;

import org.springframework.http.HttpStatus;

public enum CommonSuccessCode implements BaseSuccessCode {
    OK(HttpStatus.OK, "SUCCESS_200", "요청이 성공했습니다."),
    CREATED(HttpStatus.CREATED, "SUCCESS_201", "리소스가 생성되었습니다."),
    NO_CONTENT(HttpStatus.NO_CONTENT, "SUCCESS_204", "요청이 성공했습니다.");

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;

    CommonSuccessCode(HttpStatus httpStatus, String code, String message) {
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