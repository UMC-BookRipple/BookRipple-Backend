package com.bookripple.api.domain.blindsalepost.code;

import com.bookripple.api.global.code.BaseErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum PurchaseRequestErrorCode implements BaseErrorCode {
  BLIND_SALE_POST_NOT_FOUND(HttpStatus.NOT_FOUND, "PURCHASE_REQUEST_404_1",
      "블라인드 판매 게시글을 찾을 수 없습니다."),
  PURCHASE_REQUEST_NOT_FOUND(HttpStatus.NOT_FOUND, "PURCHASE_REQUEST_404_2",
      "구매 요청을 찾을 수 없습니다."),
  BUYER_FORBIDDEN(HttpStatus.FORBIDDEN, "PURCHASE_REQUEST_403_1",
      "구매 요청에 대한 구매자 권한이 없습니다."),
  SELLER_FORBIDDEN(HttpStatus.FORBIDDEN, "PURCHASE_REQUEST_403_2",
      "구매 요청에 대한 판매자 권한이 없습니다."),
  INVALID_STATUS(HttpStatus.BAD_REQUEST, "PURCHASE_REQUEST_400_1",
      "허용되지 않은 상태 전이입니다."),
  SELF_PURCHASE_NOT_ALLOWED(HttpStatus.BAD_REQUEST, "PURCHASE_REQUEST_400_2",
      "본인의 판매 게시글에는 구매 요청을 할 수 없습니다.");

  private final HttpStatus httpStatus;
  private final String code;
  private final String message;
}