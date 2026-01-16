package com.bookripple.api.domain.review.converter;

import com.bookripple.api.domain.member.entity.Member;
import com.bookripple.api.domain.review.entity.Review;
import com.bookripple.api.domain.review.entity.ReviewMemo;
import com.bookripple.api.global.dto.GlobalDto.ContentReq;

public class ReviewMemoConverter {

  public static ReviewMemo toReviewMemo(Review review, Member member, ContentReq request) {
    return ReviewMemo.builder()
        .review(review)
        .member(member)
        .content(request.content())
        .build();
  }
}
