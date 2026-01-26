package com.bookripple.api.domain.question.service;

import com.bookripple.api.domain.question.dto.AnswerResDto.AnswerList;
import com.bookripple.api.domain.question.dto.AnswerResDto.MyAnswerList;
import com.bookripple.api.global.dto.GlobalDto.ContentReq;
import com.bookripple.api.global.dto.GlobalDto.IdRes;

public interface AnswerService {

  IdRes createAnswer(Long questionId, Long memberId, ContentReq request);

  IdRes updateAnswer(Long answerId, Long memberId, ContentReq request);

  IdRes deleteAnswer(Long answerId, Long memberId);

  AnswerList getAnswers(Long questionId, Long memberId);

  MyAnswerList getMyAnswers(Long memberId, Long lastAnswerId, int size);
}
