package com.bookripple.api.domain.question.converter;

import com.bookripple.api.domain.member.entity.Member;
import com.bookripple.api.domain.question.dto.AnswerResDto.Ans;
import com.bookripple.api.domain.question.dto.AnswerResDto.AnswerList;
import com.bookripple.api.domain.question.dto.AnswerResDto.MyAnswer;
import com.bookripple.api.domain.question.dto.AnswerResDto.MyAnswerList;
import com.bookripple.api.domain.question.entity.Answer;
import com.bookripple.api.domain.question.entity.Question;
import java.util.List;

public class AnswerConverter {

  public static Answer toAnswer(Question question, Member member, String content) {
    return Answer.builder()
        .content(content)
        .member(member)
        .question(question)
        .build();
  }

  public static Ans toAns(Answer answer, Boolean isMine) {
    return Ans.builder()
        .id(answer.getId())
        .isMine(isMine)
        .content(answer.getContent())
        .updatedAt(answer.getUpdatedAt())
        .build();
  }

  public static AnswerList toAnswerList(List<Ans> ansList) {
    return AnswerList.builder()
        .ansList(ansList)
        .build();
  }

  public static MyAnswer toMyAnswer(Answer answer) {
    return MyAnswer.builder()
        .answerContent(answer.getContent())
        .answerId(answer.getId())
        .updatedAt(answer.getUpdatedAt())
        .questionId(answer.getQuestion().getId())
        .questionContent(answer.getQuestion().getContent())
        .bookTitle(answer.getQuestion().getBook().getTitle())
        .build();
  }

  public static MyAnswerList toMyAnswerList(List<MyAnswer> myAnswers, Boolean hasNext,
      Long lastId) {
    return MyAnswerList.builder()
        .myAnswerList(myAnswers)
        .hasNext(hasNext)
        .lastAnswerId(lastId)
        .build();
  }
}
