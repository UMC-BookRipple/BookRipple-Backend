package com.bookripple.api.domain.question.service;

import com.bookripple.api.common.code.QuestionErrorCode;
import com.bookripple.api.common.error.ApiException;
import com.bookripple.api.domain.member.entity.Member;
import com.bookripple.api.domain.member.repository.MemberRepository;
import com.bookripple.api.domain.question.converter.AnswerConverter;
import com.bookripple.api.domain.question.entity.Answer;
import com.bookripple.api.domain.question.entity.Question;
import com.bookripple.api.domain.question.repository.AnswerRepository;
import com.bookripple.api.domain.question.repository.QuestionRepository;
import com.bookripple.api.global.converter.GlobalConverter;
import com.bookripple.api.global.dto.GlobalDto.ContentReq;
import com.bookripple.api.global.dto.GlobalDto.IdRes;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@AllArgsConstructor
public class AnswerServiceImpl implements AnswerService {

  private final QuestionRepository questionRepository;
  private final MemberRepository memberRepository;
  private final AnswerRepository answerRepository;

  @Override
  @Transactional
  public IdRes createAnswer(Long questionId, Long memberId, ContentReq request) {
    Question question = questionRepository.findById(questionId)
        .orElseThrow(() -> new ApiException(QuestionErrorCode.QUESTION_NOT_FOUND));

    Member member = memberRepository.getReferenceById(memberId);

    Answer answer = AnswerConverter.toAnswer(question, member, request.content());

    answerRepository.save(answer);

    return GlobalConverter.toIdRes(answer.getId());
  }
}
