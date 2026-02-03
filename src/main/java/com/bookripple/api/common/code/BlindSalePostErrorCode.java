package com.bookripple.api.common.code;


import org.springframework.http.HttpStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum BlindSalePostErrorCode implements BaseErrorCode {
    // 404 NOT FOUND
    POST_NOT_FOUND(HttpStatus.NOT_FOUND, "POST_404_1", "해당 판매 게시글을 찾을 수 없습니다."),

    // 403 FORBIDDEN
    NOT_POST_OWNER(HttpStatus.FORBIDDEN, "POST_403_1", "게시글 수정 및 삭제 권한이 없습니다."),

    // 400 BAD REQUEST
    ALREADY_SOLD_OUT(HttpStatus.BAD_REQUEST, "POST_400_1", "이미 판매 완료된 게시글은 수정하거나 삭제할 수 없습니다."),
    ALREADY_RESERVED(HttpStatus.BAD_REQUEST, "POST_400_2", "예약 중인 게시글은 내용을 수정할 수 없습니다."),
    INVALID_POST_STATUS(HttpStatus.BAD_REQUEST, "POST_400_3", "올바르지 않은 게시글 상태입니다.");

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;
}
