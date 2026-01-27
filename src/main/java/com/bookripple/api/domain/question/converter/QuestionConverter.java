package com.bookripple.api.domain.question.converter;

import com.bookripple.api.domain.book.entity.Book;
import com.bookripple.api.domain.member.entity.Member;
import com.bookripple.api.domain.question.dto.QuestionResDto.MyQ;
import com.bookripple.api.domain.question.dto.QuestionResDto.MyQuestionList;
import com.bookripple.api.domain.question.dto.QuestionResDto.Q;
import com.bookripple.api.domain.question.dto.QuestionResDto.QuestionList;
import com.bookripple.api.domain.question.dto.QuestionResDto.ReadingAiQnA;
import com.bookripple.api.domain.question.dto.QuestionResDto.ReadingAiQnAList;
import com.bookripple.api.domain.question.entity.Question;
import com.bookripple.api.domain.question.entity.ReadingQuestion;
import com.bookripple.api.domain.question.enums.QuestionType;
import java.util.List;

public class QuestionConverter {

  public static Question toQuestion(Member member, Book book, String content, QuestionType type) {
    return Question.builder()
        .book(book)
        .content(content)
        .member(member)
        .type(type)
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

  public static MyQ toMyQ(Question question) {
    return MyQ.builder()
        .bookTitle(question.getBook().getTitle())
        .content(question.getContent())
        .createdAt(question.getCreatedAt())
        .id(question.getId())
        .type(question.getType())
        .build();
  }

  public static MyQuestionList toMyQuestionList(List<MyQ> questionList, String lastBookTitle,
      Long lastId, Boolean hasNext) {
    return MyQuestionList.builder()
        .hasNext(hasNext)
        .lastBookTitle(lastBookTitle)
        .questionList(questionList)
        .lastId(lastId)
        .build();
  }

  public static ReadingQuestion toReadingQuestion(Member member, Book book, String question) {
    return ReadingQuestion.builder()
        .book(book)
        .type(QuestionType.AI_DURING_READING)
        .question(question)
        .member(member)
        .build();
  }

  public static Q toQ(ReadingQuestion question) {
    return Q.builder()
        .id(question.getId())
        .type(question.getType())
        .content(question.getQuestion())
        .createdAt(question.getCreatedAt())
        .build();
  }

  public static ReadingAiQnA toReadingAiQnA(ReadingQuestion question) {
    return ReadingAiQnA.builder()
        .answer(question.getAnswer())
        .id(question.getId())
        .question(question.getQuestion())
        .type(question.getType())
        .updatedAt(question.getUpdatedAt())
        .build();
  }

  public static ReadingAiQnAList toReadingAiQnAList(List<ReadingAiQnA> readingAiQnAS,
      Boolean hasNext, Long lastId) {
    return ReadingAiQnAList.builder()
        .readingAiQnAS(readingAiQnAS)
        .hasNext(hasNext)
        .lastId(lastId)
        .build();
  }
}
