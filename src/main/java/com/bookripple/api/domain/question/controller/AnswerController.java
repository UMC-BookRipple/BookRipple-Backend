package com.bookripple.api.domain.question.controller;

import com.bookripple.api.common.code.CommonSuccessCode;
import com.bookripple.api.common.response.ApiResponse;
import com.bookripple.api.domain.question.service.AnswerService;
import com.bookripple.api.global.dto.GlobalDto.ContentReq;
import com.bookripple.api.global.dto.GlobalDto.IdRes;
import com.bookripple.api.global.validation.ValidationGroups;
import lombok.AllArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
@Validated
@RequestMapping(value = "/api/v1")
public class AnswerController {

  private final AnswerService answerService;

  @PostMapping("/questions/{question-id}/answers")
  public ApiResponse<IdRes> createAnswer(
      @AuthenticationPrincipal Long memberId,
      @PathVariable("question-id") Long questionId,
      @Validated(ValidationGroups.AnswerGroup.class) @RequestBody ContentReq request
  ) {
    return ApiResponse.onSuccess(CommonSuccessCode.CREATED,
        answerService.createAnswer(questionId, memberId, request));
  }

  @PatchMapping("/answers/{answer-id}")
  public ApiResponse<IdRes> updateAnswer(
      @AuthenticationPrincipal Long memberId,
      @PathVariable("answer-id") Long answerId,
      @Validated(ValidationGroups.AnswerGroup.class) @RequestBody ContentReq request
  ) {
    return ApiResponse.onSuccess(CommonSuccessCode.OK,
        answerService.updateAnswer(answerId, memberId, request));
  }

  @DeleteMapping("/answers/{answer-id}")
  public ApiResponse<IdRes> deleteAnswer(
      @AuthenticationPrincipal Long memberId,
      @PathVariable("answer-id") Long answerId
  ) {
    return ApiResponse.onSuccess(CommonSuccessCode.OK,
        answerService.deleteAnswer(answerId, memberId));
  }


}
