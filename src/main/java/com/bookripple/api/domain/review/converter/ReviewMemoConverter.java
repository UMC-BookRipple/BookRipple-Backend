package com.bookripple.api.domain.review.converter;

import com.bookripple.api.domain.member.entity.Member;
import com.bookripple.api.domain.review.dto.ReviewMemoResDto.MyReviewMemoList;
import com.bookripple.api.domain.review.dto.ReviewMemoResDto.ReviewAndMemo;
import com.bookripple.api.domain.review.entity.Review;
import com.bookripple.api.domain.review.entity.ReviewMemo;
import com.bookripple.api.global.dto.GlobalDto.ContentReq;
import java.util.List;

public class ReviewMemoConverter {

  public static ReviewMemo toReviewMemo(Review review, Member member, ContentReq request) {
    return ReviewMemo.builder()
        .review(review)
        .member(member)
        .content(request.content())
        .build();
  }

  public static ReviewAndMemo toReviewAndMemo(ReviewMemo reviewMemo) {
    return ReviewAndMemo.builder()
        .reviewMemoId(reviewMemo.getId())
        .memoContent(reviewMemo.getContent())
        .memoUpdatedAt(reviewMemo.getUpdatedAt())
        .reviewContent(reviewMemo.getReview().getContent())
        .reviewWriter(reviewMemo.getReview().getMember().getLoginId())
        .reviewUpdatedAt(reviewMemo.getReview().getUpdatedAt())
        .bookTitle(reviewMemo.getReview().getBook().getTitle())
        .build();
  }

  public static MyReviewMemoList toMyReviewMemoList(List<ReviewAndMemo> reviewAndMemoList,
      String lastBookTitle, Long lastId, Boolean hasNext) {
    return MyReviewMemoList.builder()
        .memoList(reviewAndMemoList)
        .lastBookTitle(lastBookTitle)
        .lastId(lastId)
        .hasNext(hasNext)
        .build();
  }
}
