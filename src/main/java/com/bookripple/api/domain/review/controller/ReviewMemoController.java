package com.bookripple.api.domain.review.controller;

import com.bookripple.api.common.code.CommonSuccessCode;
import com.bookripple.api.common.response.ApiResponse;
import com.bookripple.api.domain.review.service.ReviewMemoService;
import com.bookripple.api.global.dto.GlobalDto.ContentReq;
import com.bookripple.api.global.dto.GlobalDto.IdRes;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PatchMapping;
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
public class ReviewMemoController {

  private final ReviewMemoService reviewMemoService;

  @PostMapping("reviews/{review-id}/review-memos")
  public ApiResponse<IdRes> createReviewMemo(
      @PathVariable("review-id") @Min(1) Long reviewId,
      @RequestParam Long memberId,
      @Valid @RequestBody ContentReq request
  ) {
    return ApiResponse.onSuccess(CommonSuccessCode.CREATED,
        reviewMemoService.createReviewMemo(reviewId, memberId, request));
  }

  @PatchMapping("review-memos/{review-memo-id}")
  public ApiResponse<IdRes> updateReviewMemo(
      @PathVariable("review-memo-id") @Min(1) Long reviewMemoId,
      @RequestParam Long memberId,
      @Valid @RequestBody ContentReq request
  ) {
    return ApiResponse.onSuccess(CommonSuccessCode.OK,
        reviewMemoService.updateReviewMemo(reviewMemoId, memberId, request));
  }

  @DeleteMapping("review-memos/{review-memo-id}")
  public ApiResponse<IdRes> deleteReviewMemo(
      @PathVariable("review-memo-id") @Min(1) Long reviewMemoId,
      @RequestParam Long memberId
  ) {
    return ApiResponse.onSuccess(CommonSuccessCode.OK,
        reviewMemoService.deleteReviewMemo(reviewMemoId, memberId));
  }
}
