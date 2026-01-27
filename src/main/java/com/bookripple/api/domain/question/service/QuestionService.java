package com.bookripple.api.domain.question.service;

import com.bookripple.api.domain.question.dto.QuestionResDto.MyQuestionList;
import com.bookripple.api.domain.question.dto.QuestionResDto.Q;
import com.bookripple.api.domain.question.dto.QuestionResDto.QuestionList;
import com.bookripple.api.global.dto.GlobalDto.ContentReq;
import com.bookripple.api.global.dto.GlobalDto.IdRes;

public interface QuestionService {

  IdRes createQuestion(Long memberId, Long bookId, ContentReq request);

  IdRes deleteQuestion(Long memberId, Long questionId);

  QuestionList getQuestion(Long memberId, Long bookId, String keyword, Boolean onlyMine,
      Long lastId, int size);

  MyQuestionList getMyQuestion(Long memberId, String lastBookTitle, Long lastId, int size);

  QuestionList createAfterReadingQuestion(Long memberId, Long bookId);

  Q createDuringReadingQuestion(Long memberId, Long bookId);

  IdRes updateReadingQuestion(Long memberId, Long readingQuestionId, ContentReq request);
}
