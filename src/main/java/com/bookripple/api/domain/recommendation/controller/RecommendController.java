package com.bookripple.api.domain.recommendation.controller;

import com.bookripple.api.common.code.CommonSuccessCode;
import com.bookripple.api.common.response.ApiResponse;
import com.bookripple.api.domain.recommendation.dto.RecommendReqDto.Create;
import com.bookripple.api.domain.recommendation.service.RecommendService;
import com.bookripple.api.global.dto.GlobalDto.IdRes;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Validated
@RequestMapping(value = "/api/v1")
public class RecommendController {

  private final RecommendService recommendService;

  @PostMapping("/books/{book-id}/recommendations")
  public ApiResponse<IdRes> createRecommendation(
      @AuthenticationPrincipal Long memberId,
      @PathVariable("book-id") Long sourceBookId,
      @RequestBody @Valid Create request
  ) {
    return ApiResponse.onSuccess(CommonSuccessCode.CREATED,
        recommendService.createRecommendation(memberId, sourceBookId, request));
  }
}
