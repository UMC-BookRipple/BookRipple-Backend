package com.bookripple.api.domain.review.service;

import com.bookripple.api.global.dto.GlobalDto.ContentReq;
import com.bookripple.api.global.dto.GlobalDto.IdRes;

public interface ReviewMemoService {

  IdRes createReviewMemo(Long reviewId, Long memberId, ContentReq request);

  IdRes updateReviewMemo(Long reviewMemoId, Long memberId, ContentReq request);

  IdRes deleteReviewMemo(Long reviewMemoId, Long memberId);

}
