package com.bookripple.api.domain.review.service;

import com.bookripple.api.domain.review.dto.ReviewResDto.MyReviewList;
import com.bookripple.api.domain.review.dto.ReviewResDto.ReviewList;
import com.bookripple.api.global.dto.GlobalDto.ContentReq;
import com.bookripple.api.global.dto.GlobalDto.IdList;
import com.bookripple.api.global.dto.GlobalDto.IdRes;


public interface ReviewService {

  IdRes createReview(Long bookId, Long memberId, ContentReq request);

  IdRes deleteReview(Long reviewId, Long memberId);

  IdRes updateReview(Long reviewId, Long memberId, ContentReq request);

  ReviewList getReviews(Long bookId, Long memberId, Long lastId, int size);

  MyReviewList getMyReviews(Long memberId, String lastBookTitle, Long lastId, int size);

  void deleteMyReviews(Long memberId, IdList request);
}
