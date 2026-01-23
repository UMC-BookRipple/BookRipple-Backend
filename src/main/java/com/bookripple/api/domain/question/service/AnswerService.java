package com.bookripple.api.domain.question.service;

import com.bookripple.api.global.dto.GlobalDto.ContentReq;
import com.bookripple.api.global.dto.GlobalDto.IdRes;

public interface AnswerService {

  IdRes createAnswer(Long questionId, Long memberId, ContentReq request);

  IdRes updateAnswer(Long answerId, Long memberId, ContentReq request);

}
