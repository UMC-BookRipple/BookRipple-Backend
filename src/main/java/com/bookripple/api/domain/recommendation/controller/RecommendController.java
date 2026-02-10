package com.bookripple.api.domain.recommendation.controller;

import com.bookripple.api.common.code.CommonSuccessCode;
import com.bookripple.api.common.response.ApiResponse;
import com.bookripple.api.domain.recommendation.dto.RecommendReqDto.Create;
import com.bookripple.api.domain.recommendation.dto.RecommendResDto.MyRecommendList;
import com.bookripple.api.domain.recommendation.dto.RecommendResDto.RecommendList;
import com.bookripple.api.domain.recommendation.service.RecommendService;
import com.bookripple.api.global.annotation.PreventDuplicate;
import com.bookripple.api.global.dto.GlobalDto;
import com.bookripple.api.global.dto.GlobalDto.ContentReq;
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
public class RecommendController {

  private final RecommendService recommendService;

  @PreventDuplicate
  @PostMapping("/books/{book-id}/recommendations")
  public ApiResponse<IdRes> createRecommendation(
      @AuthenticationPrincipal Long memberId,
      @PathVariable("book-id") @Min(1) Long sourceBookId,
      @RequestBody @Validated(ValidationGroups.RecommendGroup.class) Create request
  ) {
    return ApiResponse.onSuccess(CommonSuccessCode.CREATED,
        recommendService.createRecommendation(memberId, sourceBookId, request));
  }

  @PreventDuplicate
  @PatchMapping("/recommendations/{recommendation-id}")
  public ApiResponse<IdRes> updateRecommendation(
      @AuthenticationPrincipal Long memberId,
      @PathVariable("recommendation-id") @Min(1) Long recommendationId,
      @RequestBody @Validated(ValidationGroups.RecommendGroup.class) ContentReq request
  ) {
    return ApiResponse.onSuccess(CommonSuccessCode.OK,
        recommendService.updateRecommendation(memberId, recommendationId, request));
  }

  @PreventDuplicate
  @DeleteMapping("/recommendations/{recommendation-id}")
  public ApiResponse<IdRes> deleteRecommendation(
      @AuthenticationPrincipal Long memberId,
      @PathVariable("recommendation-id") @Min(1) Long recommendationId
  ) {
    return ApiResponse.onSuccess(CommonSuccessCode.OK,
        recommendService.deleteRecommendation(memberId, recommendationId));
  }

  @GetMapping("/books/{book-id}/recommendations")
  public ApiResponse<RecommendList> getRecommendations(
      @AuthenticationPrincipal Long memberId,
      @PathVariable("book-id") @Min(1) Long sourceBookId,
      @RequestParam(required = false) @Min(1) Long lastId,
      @RequestParam(defaultValue = "3") @Max(100) int size
  ) {
    return ApiResponse.onSuccess(CommonSuccessCode.OK,
        recommendService.getRecommendList(memberId, sourceBookId, lastId, size));
  }

  @GetMapping("/recommendations/me")
  public ApiResponse<MyRecommendList> getMyRecommendations(
      @AuthenticationPrincipal Long memberId,
      @RequestParam(required = false) @Size(max = 100) String lastSourceBookTitle,
      @RequestParam(required = false) @Min(1) Long lastId,
      @RequestParam(defaultValue = "3") @Max(100) int size
  ) {
    return ApiResponse.onSuccess(CommonSuccessCode.OK,
        recommendService.getMyRecommendList(memberId, lastSourceBookTitle, lastId, size));
  }

  @PreventDuplicate
  @PostMapping("/recommendations/me/batch-delete")
  public ApiResponse<Void> deleteMyRecommendations(
      @AuthenticationPrincipal Long memberId,
      @RequestBody @Valid GlobalDto.IdList request
  ) {
    recommendService.deleteMyRecommendations(memberId, request);
    return ApiResponse.onSuccess(CommonSuccessCode.NO_CONTENT, null);
  }
}
