package com.bookripple.api.domain.review.controller;

import com.bookripple.api.common.code.CommonSuccessCode;
import com.bookripple.api.common.response.ApiResponse;
import com.bookripple.api.domain.review.service.ReviewService;
import com.bookripple.api.global.dto.GlobalDto.ContentReq;
import com.bookripple.api.global.dto.GlobalDto.IdRes;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Validated
@RequestMapping(value = "/api/v1")
public class ReviewController {

  private final ReviewService reviewService;

  @PostMapping("/books/{bookId}/reviews")
  public ApiResponse<IdRes> createReview(
      @PathVariable @Min(1) Long bookId,
      @RequestParam @Min(1) Long memberId,
      @Valid @RequestBody ContentReq request
  ) {

    return ApiResponse.onSuccess(CommonSuccessCode.CREATED,
        reviewService.createReview(bookId, memberId, request));
  }
}
