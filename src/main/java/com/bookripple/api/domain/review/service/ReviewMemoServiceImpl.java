package com.bookripple.api.domain.review.service;

import com.bookripple.api.common.code.ReviewErrorCode;
import com.bookripple.api.common.error.ApiException;
import com.bookripple.api.domain.member.entity.Member;
import com.bookripple.api.domain.member.repository.MemberRepository;
import com.bookripple.api.domain.review.converter.ReviewMemoConverter;
import com.bookripple.api.domain.review.entity.Review;
import com.bookripple.api.domain.review.entity.ReviewMemo;
import com.bookripple.api.domain.review.repository.ReviewMemoRepository;
import com.bookripple.api.domain.review.repository.ReviewRepository;
import com.bookripple.api.global.converter.GlobalConverter;
import com.bookripple.api.global.dto.GlobalDto.ContentReq;
import com.bookripple.api.global.dto.GlobalDto.IdRes;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class ReviewMemoServiceImpl implements ReviewMemoService {

  ReviewMemoRepository reviewMemoRepository;
  ReviewRepository reviewRepository;
  MemberRepository memberRepository;

  @Override
  public IdRes createReviewMemo(Long reviewId, Long memberId, ContentReq request) {
    Review review = reviewRepository.findById(reviewId)
        .orElseThrow(() -> new ApiException(ReviewErrorCode.NO_REVIEW));
    Member member = memberRepository.getReferenceById(memberId);

    if (reviewMemoRepository.existsByReviewAndMember(review, member)) {
      throw new ApiException(ReviewErrorCode.MEMO_ALREADY_EXISTS);
    }

    ReviewMemo reviewMemo = ReviewMemoConverter.toReviewMemo(review, member, request);

    reviewMemoRepository.save(reviewMemo);

    return GlobalConverter.toIdRes(reviewMemo.getId());
  }
}
