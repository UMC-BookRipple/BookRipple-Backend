package com.bookripple.api.domain.review.converter;

import com.bookripple.api.domain.book.entity.Book;
import com.bookripple.api.domain.member.entity.Member;
import com.bookripple.api.domain.review.dto.ReviewResDto.Item;
import com.bookripple.api.domain.review.dto.ReviewResDto.ReviewList;
import com.bookripple.api.domain.review.entity.Review;
import com.bookripple.api.global.dto.GlobalDto;
import java.util.List;

public class ReviewConverter {

  public static Review toReview(GlobalDto.ContentReq request, Member member, Book book) {
    return Review.builder()
        .content(request.content())
        .member(member)
        .book(book)
        .build();
  }

  public static Item toItem(Review review) {
    return Item.builder()
        .id(review.getId())
        .content(review.getContent())
        .nickname(review.getMember().getLoginId())
        .updatedAt(review.getUpdatedAt())
        .build();
  }

  public static ReviewList toReviewList(List<Item> reviewList, Long lastId, Boolean hasNext) {
    return ReviewList.builder()
        .reviewList(reviewList)
        .hasNext(hasNext)
        .lastId(lastId)
        .build();
  }
}
