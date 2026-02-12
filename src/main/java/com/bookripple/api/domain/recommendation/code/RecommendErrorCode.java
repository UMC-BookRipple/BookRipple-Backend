package com.bookripple.api.domain.recommendation.code;

import com.bookripple.api.common.code.BaseErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum RecommendErrorCode implements BaseErrorCode {
  NO_RECOMMENDATION(HttpStatus.NOT_FOUND, "RECOMMENDATION_404", "추천 글을 찾을 수 없습니다."),
  FORBIDDEN(HttpStatus.FORBIDDEN, "RECOMMENDATION_403", "추천 글에 대한 권한이 없습니다.");

  private final HttpStatus httpStatus;
  private final String code;
  private final String message;
}
