package com.bookripple.api.common.code;


import org.springframework.http.HttpStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum TradeErrorCode implements BaseErrorCode {
    // 404 NOT FOUND
    TRADE_NOT_FOUND(404, "TRADE_404", "해당 거래를 찾을 수 없습니다."),
    // 403 FORBIDDEN
    NOT_TRADE_PARTICIPANT(403, "TRADE_403", "거래 참여자가 아닙니다."),
    // 400 BAD REQUEST
    INVALID_TRADE_STATUS(400, "TRADE_400_1", "올바르지 않은 거래 상태입니다."),
    TRADE_ALREADY_PREPARED(400, "TRADE_400_2", "이미 결제 준비가 완료되었습니다."),
    TRADE_NOT_COMPLETED(400, "TRADE_400_3", "결제가 완료되지 않았습니다."),
    TRADE_ALREADY_COMPLETED(400, "TRADE_400_4", "이미 정산이 완료되었습니다.");

    private final int httpStatus;
    private final String code;
    private final String message;
}
