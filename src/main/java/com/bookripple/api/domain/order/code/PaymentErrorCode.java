package com.bookripple.api.domain.order.code;

import com.bookripple.api.global.code.BaseErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum PaymentErrorCode implements BaseErrorCode {
  PAYMENT_NOT_FOUND(HttpStatus.NOT_FOUND, "PAYMENT_404", "결제를 찾을 수 없습니다."),
  NOTIFICATION_FORBIDDEN(HttpStatus.FORBIDDEN, "PAYMENT_403", "결제에 대한 권한이 없습니다.");

  private final HttpStatus httpStatus;
  private final String code;
  private final String message;
}