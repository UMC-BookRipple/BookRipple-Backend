package com.bookripple.api.domain.question.converter;

import com.bookripple.api.domain.member.entity.Member;
import com.bookripple.api.domain.question.entity.Answer;
import com.bookripple.api.domain.question.entity.Question;

public class AnswerConverter {

  public static Answer toAnswer(Question question, Member member, String content) {
    return Answer.builder()
        .content(content)
        .member(member)
        .question(question)
        .build();
  }
}
