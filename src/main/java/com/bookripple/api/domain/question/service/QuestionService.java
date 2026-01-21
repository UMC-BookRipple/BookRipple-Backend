package com.bookripple.api.domain.question.service;

import com.bookripple.api.global.dto.GlobalDto.ContentReq;
import com.bookripple.api.global.dto.GlobalDto.IdRes;

public interface QuestionService {

  IdRes createQuestion(Long memberId, Long bookId, ContentReq request);
}
