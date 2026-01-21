package com.bookripple.api.domain.recommendation.converter;

import com.bookripple.api.domain.book.entity.Book;
import com.bookripple.api.domain.member.entity.Member;
import com.bookripple.api.domain.recommendation.dto.RecommendResDto.Recommend;
import com.bookripple.api.domain.recommendation.dto.RecommendResDto.RecommendList;
import com.bookripple.api.domain.recommendation.entity.Recommendation;
import java.util.List;

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

  public static Recommend toRecommend(Recommendation recommendation) {
    return Recommend.builder()
        .id(recommendation.getId())
        .updatedAt(recommendation.getUpdatedAt())
        .sourceBookTitle(recommendation.getSourceBook().getTitle())
        .targetBookAuthor(recommendation.getTargetBook().getAuthor())
        .targetBookCover(recommendation.getTargetBook().getBookCover())
        .targetBookId(recommendation.getTargetBook().getId())
        .targetBookTitle(recommendation.getTargetBook().getTitle())
        .content(recommendation.getContent())
        .nickname(recommendation.getMember().getLoginId())
        .build();
  }

  public static RecommendList toRecommendList(List<Recommend> recommendListList, Long lastId,
      Boolean hasNext) {
    return RecommendList.builder()
        .recommendList(recommendListList)
        .hasNext(hasNext)
        .lastId(lastId)
        .build();
  }
}
