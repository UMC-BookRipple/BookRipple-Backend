package com.bookripple.api.common.code;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

public enum MemoErrorCode implements BaseErrorCode {  // 프로젝트의 ErrorCode 인터페이스에 맞게
    NO_MEMO(HttpStatus.NOT_FOUND,"MEMO_404", "메모를 찾을 수 없습니다."),
    FORBIDDEN(HttpStatus.FORBIDDEN, "MEMO_403", "메모에 대한 권한이 없습니다.");

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;

    MemoErrorCode(HttpStatus httpStatus, String code, String message) {
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


