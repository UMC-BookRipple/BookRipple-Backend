package com.bookripple.api.domain.review.controller;

import com.bookripple.api.common.code.CommonSuccessCode;
import com.bookripple.api.common.response.ApiResponse;
import com.bookripple.api.domain.review.dto.ReviewResDto.MyReviewList;
import com.bookripple.api.domain.review.dto.ReviewResDto.ReviewList;
import com.bookripple.api.domain.review.service.ReviewService;
import com.bookripple.api.global.dto.GlobalDto.ContentReq;
import com.bookripple.api.global.dto.GlobalDto.IdList;
import com.bookripple.api.global.dto.GlobalDto.IdRes;
import com.bookripple.api.global.validation.ValidationGroups;
import jakarta.validation.Valid;
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
public class ReviewController {

  private final ReviewService reviewService;

  @PostMapping("/books/{book-id}/reviews")
  public ApiResponse<IdRes> createReview(
      @PathVariable("book-id") @Min(1) Long bookId,
      @AuthenticationPrincipal Long memberId,
      @Validated(ValidationGroups.ReviewGroup.class) @RequestBody ContentReq request
  ) {
    return ApiResponse.onSuccess(CommonSuccessCode.CREATED,
        reviewService.createReview(bookId, memberId, request));
  }

  @DeleteMapping("/reviews/{review-id}")
  public ApiResponse<IdRes> deleteReview(
      @AuthenticationPrincipal Long memberId,
      @PathVariable("review-id") @Min(1) Long reviewId
  ) {
    return ApiResponse.onSuccess(CommonSuccessCode.OK,
        reviewService.deleteReview(reviewId, memberId));
  }

  @PatchMapping("/reviews/{review-id}")
  public ApiResponse<IdRes> updateReview(
      @PathVariable("review-id") @Min(1) Long reviewId,
      @AuthenticationPrincipal Long memberId,
      @Validated(ValidationGroups.ReviewGroup.class) @RequestBody ContentReq request
  ) {
    return ApiResponse.onSuccess(CommonSuccessCode.OK,
        reviewService.updateReview(reviewId, memberId, request));
  }

  @GetMapping("/books/{book-id}/reviews")
  public ApiResponse<ReviewList> getReviews(
      @PathVariable("book-id") @Min(1) Long bookId,
      @AuthenticationPrincipal Long memberId,
      @RequestParam(required = false) @Min(1) Long lastId,
      @RequestParam(defaultValue = "3") @Max(100) int size
  ) {
    return ApiResponse.onSuccess(CommonSuccessCode.OK,
        reviewService.getReviews(bookId, memberId, lastId, size));
  }

  @GetMapping("/reviews/me")
  public ApiResponse<MyReviewList> getMyReviews(
      @AuthenticationPrincipal Long memberId,
      @RequestParam(required = false) @Size(max = 100) String lastBookTitle,
      @RequestParam(required = false) @Min(1) Long lastId,
      @RequestParam(defaultValue = "3") @Max(100) int size
  ) {
    return ApiResponse.onSuccess(CommonSuccessCode.OK,
        reviewService.getMyReviews(memberId, lastBookTitle, lastId, size));
  }

  @PostMapping("/reviews/me/batch-delete")
  public ApiResponse<Void> deleteMyReviews(
      @AuthenticationPrincipal Long memberId,
      @RequestBody @Valid IdList request
  ) {
    reviewService.deleteMyReviews(memberId, request);
    return ApiResponse.onSuccess(CommonSuccessCode.NO_CONTENT, null);
  }
}
