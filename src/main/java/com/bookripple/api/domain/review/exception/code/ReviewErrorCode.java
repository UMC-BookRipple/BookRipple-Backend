package com.bookripple.api.domain.review.exception.code;

import com.bookripple.api.common.code.BaseErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ReviewErrorCode implements BaseErrorCode {
  NO_REVIEW(HttpStatus.NOT_FOUND, "REVIEW_404", "리뷰를 찾을 수 없습니다."),
  FORBIDDEN(HttpStatus.FORBIDDEN, "REVIEW_403", "리뷰에 대한 권한이 없습니다.");
  private final HttpStatus httpStatus;
  private final String code;
  private final String message;
}
