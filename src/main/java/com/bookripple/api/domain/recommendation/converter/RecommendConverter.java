package com.bookripple.api.domain.recommendation.converter;

import com.bookripple.api.domain.book.entity.Book;
import com.bookripple.api.domain.member.entity.Member;
import com.bookripple.api.domain.recommendation.entity.Recommendation;

public class RecommendConverter {

  public static Recommendation toRecommendation(Member member, Book sourceBook, Book targetBook,
      String content) {
    return Recommendation.builder()
        .content(content)
        .sourceBook(sourceBook)
        .targetBook(targetBook)
        .member(member)
        .build();
  }

}
