package com.bookripple.api.domain.question.controller;

import com.bookripple.api.common.code.CommonSuccessCode;
import com.bookripple.api.common.response.ApiResponse;
import com.bookripple.api.domain.question.dto.QuestionResDto.MyQuestionList;
import com.bookripple.api.domain.question.dto.QuestionResDto.Q;
import com.bookripple.api.domain.question.dto.QuestionResDto.QuestionList;
import com.bookripple.api.domain.question.dto.QuestionResDto.ReadingAiQnAList;
import com.bookripple.api.domain.question.service.QuestionService;
import com.bookripple.api.global.dto.GlobalDto.ContentReq;
import com.bookripple.api.global.dto.GlobalDto.IdRes;
import com.bookripple.api.global.validation.ValidationGroups;
import jakarta.validation.constraints.Min;
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
public class QuestionController {

  private final QuestionService questionService;

  @PostMapping("/books/{book-id}/questions")
  public ApiResponse<IdRes> createQuestion(
      @AuthenticationPrincipal Long memberId,
      @PathVariable("book-id") @Min(1) Long bookId,
      @Validated(ValidationGroups.QuestionGroup.class) @RequestBody ContentReq request
  ) {
    return ApiResponse.onSuccess(CommonSuccessCode.CREATED,
        questionService.createQuestion(memberId, bookId, request));
  }

  @DeleteMapping("/questions/{question-id}")
  public ApiResponse<IdRes> deleteQuestion(
      @AuthenticationPrincipal Long memberId,
      @PathVariable("question-id") Long questionId
  ) {
    return ApiResponse.onSuccess(CommonSuccessCode.OK,
        questionService.deleteQuestion(memberId, questionId));
  }

  @GetMapping("/books/{book-id}/questions")
  public ApiResponse<QuestionList> getQuestion(
      @AuthenticationPrincipal Long memberId,
      @PathVariable("book-id") @Min(1) Long bookId,
      @RequestParam(required = false) String keyword,
      @RequestParam(defaultValue = "false") Boolean onlyMine,
      @RequestParam(required = false) Long lastId,
      @RequestParam(defaultValue = "3") int size
  ) {
    return ApiResponse.onSuccess(CommonSuccessCode.OK,
        questionService.getQuestion(memberId, bookId, keyword, onlyMine, lastId, size));
  }

  @GetMapping("/questions/me")
  public ApiResponse<MyQuestionList> getMyQuestion(
      @AuthenticationPrincipal Long memberId,
      @RequestParam(required = false) String lastBookTitle,
      @RequestParam(required = false) Long lastId,
      @RequestParam(defaultValue = "3") int size
  ) {
    return ApiResponse.onSuccess(CommonSuccessCode.OK,
        questionService.getMyQuestion(memberId, lastBookTitle, lastId, size));
  }

  @PostMapping("/books/{book-id}/questions/ai/after")
  public ApiResponse<QuestionList> createAfterReadingQuestion(
      @AuthenticationPrincipal Long memberId,
      @PathVariable("book-id") Long bookId
  ) {
    return ApiResponse.onSuccess(CommonSuccessCode.OK,
        questionService.createAfterReadingQuestion(memberId, bookId));
  }

  @PostMapping("/books/{book-id}/questions/ai/during")
  public ApiResponse<Q> createDuringReadingQuestion(
      @AuthenticationPrincipal Long memberId,
      @PathVariable("book-id") Long bookId
  ) {
    return ApiResponse.onSuccess(CommonSuccessCode.OK,
        questionService.createDuringReadingQuestion(memberId, bookId));
  }

  @PatchMapping("/reading-questions/{reading-question-id}")
  public ApiResponse<IdRes> updateReadingQuestion(
      @AuthenticationPrincipal Long memberId,
      @PathVariable("reading-question-id") @Min(1) Long readingQuestionId,
      @RequestBody @Validated(ValidationGroups.AnswerGroup.class) ContentReq request
  ) {
    return ApiResponse.onSuccess(CommonSuccessCode.OK,
        questionService.updateReadingQuestion(memberId, readingQuestionId, request));
  }

  @GetMapping("/books/{book-id}/reading-questions")
  public ApiResponse<ReadingAiQnAList> getReadingAiQnA(
      @AuthenticationPrincipal Long memberId,
      @PathVariable("book-id") @Min(1) Long bookId,
      @RequestParam(required = false) Long lastId,
      @RequestParam(defaultValue = "3") Integer size
  ) {
    return ApiResponse.onSuccess(CommonSuccessCode.OK,
        questionService.getReadingAiQnAList(memberId, bookId, lastId, size));
  }

  @DeleteMapping("/reading-questions/{reading-question-id}")
  public ApiResponse<IdRes> deleteReadingAiQnA(
      @AuthenticationPrincipal Long memberId,
      @PathVariable("reading-question-id") @Min(1) Long readingQuestionId
  ) {
    return ApiResponse.onSuccess(CommonSuccessCode.OK,
        questionService.deleteReadingAiQnA(memberId, readingQuestionId));
  }

  @GetMapping("/books/{book-id}/search")
  public ApiResponse<QuestionList> searchQuestion(
      @AuthenticationPrincipal Long memberId,
      @PathVariable("book-id") @Min(1) Long bookId,
      @RequestParam String query,
      @RequestParam(defaultValue = "0") @Min(0) int page,
      @RequestParam(defaultValue = "20") @Min(1) int size
  ) {
    return ApiResponse.onSuccess(CommonSuccessCode.OK,
        questionService.searchQuestion(memberId, bookId, query, page, size));
  }

  @GetMapping("/community/search/history")
  public ApiResponse<QuestionList> getSearchHistory(
      @AuthenticationPrincipal Long memberId
  ) {
    return ApiResponse.onSuccess(CommonSuccessCode.OK,
        questionService.getSearchHistory(memberId));
  }

  @DeleteMapping("/community/search/history/{history-id}")
  public ApiResponse<IdRes> deleteSearchHistory(
      @AuthenticationPrincipal Long memberId,
      @PathVariable("history-id") @Min(1) Long historyId
  ) {
    return ApiResponse.onSuccess(CommonSuccessCode.OK,
        questionService.deleteSearchHistory(memberId, historyId));
  }

  @DeleteMapping("/community/search/history")
  public ApiResponse<Void> deleteAllSearchHistory(
      @AuthenticationPrincipal Long memberId
  ) {
    questionService.deleteAllSearchHistory(memberId);
    return ApiResponse.onSuccess(CommonSuccessCode.OK, null);
  }

}
