package com.bookripple.api.domain.review.converter;

import com.bookripple.api.domain.book.entity.Book;
import com.bookripple.api.domain.member.entity.Member;
import com.bookripple.api.domain.review.entity.Review;
import com.bookripple.api.global.dto.GlobalDto;

public class ReviewConverter {

  public static Review toReview(GlobalDto.ContentReq request, Member member, Book book) {
    return Review.builder()
        .content(request.content())
        .member(member)
        .book(book)
        .build();
  }
}
