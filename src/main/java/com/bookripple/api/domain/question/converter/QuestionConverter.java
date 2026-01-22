package com.bookripple.api.domain.question.converter;

import com.bookripple.api.domain.book.entity.Book;
import com.bookripple.api.domain.member.entity.Member;
import com.bookripple.api.domain.question.dto.QuestionResDto.Q;
import com.bookripple.api.domain.question.dto.QuestionResDto.QuestionList;
import com.bookripple.api.domain.question.entity.Question;
import com.bookripple.api.global.enums.QuestionType;
import java.util.List;

public class QuestionConverter {

  public static Question toQuestion(Member member, Book book, String content) {
    return Question.builder()
        .book(book)
        .content(content)
        .member(member)
        .type(QuestionType.USER)
        .build();
  }

  public static Q toQ(Question question) {
    return Q.builder()
        .id(question.getId())
        .type(question.getType())
        .content(question.getContent())
        .createdAt(question.getCreatedAt())
        .build();
  }

  public static QuestionList toQuestionList(List<Q> questionList, Long lastId,
      Boolean hasNext, long totalCnt) {
    return QuestionList.builder()
        .totalCnt(totalCnt)
        .questionList(questionList)
        .hasNext(hasNext)
        .lastId(lastId)
        .build();
  }

}
