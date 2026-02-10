package com.bookripple.api.common.code;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum SettlementErrorCode implements BaseErrorCode {
    SETTLEMENT_NOT_FOUND(HttpStatus.NOT_FOUND, "SETTLEMENT_404", "정산을 찾을 수 없습니다."),
    SETTLEMENT_FORBIDDEN(HttpStatus.FORBIDDEN, "SETTLEMENT_403", "금지된 정산입니다.");

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;
}