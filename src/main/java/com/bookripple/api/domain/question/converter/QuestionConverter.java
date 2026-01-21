package com.bookripple.api.domain.question.converter;

import com.bookripple.api.domain.book.entity.Book;
import com.bookripple.api.domain.member.entity.Member;
import com.bookripple.api.domain.question.entity.Question;
import com.bookripple.api.global.enums.QuestionType;

public class QuestionConverter {

  public static Question toQuestion(Member member, Book book, String content) {
    return Question.builder()
        .book(book)
        .content(content)
        .member(member)
        .type(QuestionType.USER)
        .build();
  }

}
