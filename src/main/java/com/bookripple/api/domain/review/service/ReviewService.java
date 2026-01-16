package com.bookripple.api.domain.review.service;

import com.bookripple.api.global.dto.GlobalDto.ContentReq;
import com.bookripple.api.global.dto.GlobalDto.IdRes;


public interface ReviewService {

  IdRes createReview(Long bookId, Long memberId, ContentReq request);

  IdRes deleteReview(Long reviewId, Long memberId);
}
