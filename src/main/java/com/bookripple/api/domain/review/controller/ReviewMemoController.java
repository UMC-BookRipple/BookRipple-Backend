package com.bookripple.api.domain.review.controller;

import com.bookripple.api.domain.review.dto.ReviewMemoResDto.MyReviewMemoList;
import com.bookripple.api.domain.review.service.ReviewMemoService;
import com.bookripple.api.global.code.CommonSuccessCode;
import com.bookripple.api.global.dto.GlobalDto.ContentReq;
import com.bookripple.api.global.dto.GlobalDto.IdRes;
import com.bookripple.api.global.response.ApiResponse;
import com.bookripple.api.global.validation.ValidationGroups;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
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

  @PostMapping("/reviews/{review-id}/review-memos")
  public ApiResponse<IdRes> createReviewMemo(
      @PathVariable("review-id") @Min(1) Long reviewId,
      @AuthenticationPrincipal Long memberId,
      @Validated(ValidationGroups.MemoGroup.class) @RequestBody ContentReq request
  ) {
    return ApiResponse.onSuccess(CommonSuccessCode.CREATED,
        reviewMemoService.createReviewMemo(reviewId, memberId, request));
  }

  @PatchMapping("/review-memos/{review-memo-id}")
  public ApiResponse<IdRes> updateReviewMemo(
      @PathVariable("review-memo-id") @Min(1) Long reviewMemoId,
      @AuthenticationPrincipal Long memberId,
      @Validated(ValidationGroups.MemoGroup.class) @RequestBody ContentReq request
  ) {
    return ApiResponse.onSuccess(CommonSuccessCode.OK,
        reviewMemoService.updateReviewMemo(reviewMemoId, memberId, request));
  }

  @DeleteMapping("/review-memos/{review-memo-id}")
  public ApiResponse<IdRes> deleteReviewMemo(
      @PathVariable("review-memo-id") @Min(1) Long reviewMemoId,
      @AuthenticationPrincipal Long memberId
  ) {
    return ApiResponse.onSuccess(CommonSuccessCode.OK,
        reviewMemoService.deleteReviewMemo(reviewMemoId, memberId));
  }

  @GetMapping("/review-memos/me")
  public ApiResponse<MyReviewMemoList> getMyReviewMemo(
      @AuthenticationPrincipal @Min(1) Long memberId,
      @RequestParam(required = false) @Size(max = 100) String lastBookTitle,
      @RequestParam(required = false) @Min(1) Long lastReviewMemoId,
      @RequestParam(defaultValue = "3") @Max(100) int size
  ) {

    return ApiResponse.onSuccess(CommonSuccessCode.OK,
        reviewMemoService.getMyReviewMemoList(memberId, lastBookTitle, lastReviewMemoId, size));
  }
}
