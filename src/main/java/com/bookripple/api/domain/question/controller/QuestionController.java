package com.bookripple.api.domain.question.controller;

import com.bookripple.api.common.code.CommonSuccessCode;
import com.bookripple.api.common.response.ApiResponse;
import com.bookripple.api.domain.question.service.QuestionService;
import com.bookripple.api.global.dto.GlobalDto.ContentReq;
import com.bookripple.api.global.dto.GlobalDto.IdRes;
import com.bookripple.api.global.validation.ValidationGroups;
import jakarta.validation.constraints.Min;
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
public class QuestionController {

  private final QuestionService questionService;

  @PostMapping("books/{book-id}/questions")
  public ApiResponse<IdRes> createQuestion(
      @AuthenticationPrincipal Long memberId,
      @PathVariable("book-id") @Min(1) Long bookId,
      @Validated(ValidationGroups.QuestionGroup.class) @RequestBody ContentReq request
  ) {
    return ApiResponse.onSuccess(CommonSuccessCode.CREATED,
        questionService.createQuestion(memberId, bookId, request));
  }
}
