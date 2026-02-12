package com.bookripple.api.domain.question.controller;

import com.bookripple.api.domain.question.dto.AnswerResDto.AnswerList;
import com.bookripple.api.domain.question.dto.AnswerResDto.MyAnswerList;
import com.bookripple.api.domain.question.service.AnswerService;
import com.bookripple.api.global.annotation.PreventDuplicate;
import com.bookripple.api.global.code.CommonSuccessCode;
import com.bookripple.api.global.dto.GlobalDto.ContentReq;
import com.bookripple.api.global.dto.GlobalDto.IdRes;
import com.bookripple.api.global.response.ApiResponse;
import com.bookripple.api.global.validation.ValidationGroups;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
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
@AllArgsConstructor
@Validated
@RequestMapping(value = "/api/v1")
public class AnswerController {

  private final AnswerService answerService;

  @PreventDuplicate
  @PostMapping("/questions/{question-id}/answers")
  public ApiResponse<IdRes> createAnswer(
      @AuthenticationPrincipal Long memberId,
      @PathVariable("question-id") @Min(1) Long questionId,
      @Validated(ValidationGroups.AnswerGroup.class) @RequestBody ContentReq request
  ) {
    return ApiResponse.onSuccess(CommonSuccessCode.CREATED,
        answerService.createAnswer(questionId, memberId, request));
  }

  @PreventDuplicate
  @PatchMapping("/answers/{answer-id}")
  public ApiResponse<IdRes> updateAnswer(
      @AuthenticationPrincipal Long memberId,
      @PathVariable("answer-id") @Min(1) Long answerId,
      @Validated(ValidationGroups.AnswerGroup.class) @RequestBody ContentReq request
  ) {
    return ApiResponse.onSuccess(CommonSuccessCode.OK,
        answerService.updateAnswer(answerId, memberId, request));
  }

  @PreventDuplicate
  @DeleteMapping("/answers/{answer-id}")
  public ApiResponse<IdRes> deleteAnswer(
      @AuthenticationPrincipal Long memberId,
      @PathVariable("answer-id") @Min(1) Long answerId
  ) {
    return ApiResponse.onSuccess(CommonSuccessCode.OK,
        answerService.deleteAnswer(answerId, memberId));
  }

  @GetMapping("/questions/{question-id}/answers")
  public ApiResponse<AnswerList> getAnswerList(
      @AuthenticationPrincipal Long memberId,
      @PathVariable("question-id") @Min(1) Long questionId
  ) {
    return ApiResponse.onSuccess(CommonSuccessCode.OK,
        answerService.getAnswers(questionId, memberId));
  }

  @GetMapping("/answers/me")
  public ApiResponse<MyAnswerList> getMyAnswerList(
      @AuthenticationPrincipal Long memberId,
      @RequestParam(required = false) @Min(1) Long lastAnswerId,
      @RequestParam(defaultValue = "3") @Max(100) int size
  ) {
    return ApiResponse.onSuccess(CommonSuccessCode.OK,
        answerService.getMyAnswers(memberId, lastAnswerId, size));
  }
}
