package com.bookripple.api.domain.review.service;

import com.bookripple.api.common.code.ReviewErrorCode;
import com.bookripple.api.common.error.ApiException;
import com.bookripple.api.domain.member.entity.Member;
import com.bookripple.api.domain.member.repository.MemberRepository;
import com.bookripple.api.domain.review.converter.ReviewMemoConverter;
import com.bookripple.api.domain.review.dto.ReviewMemoResDto.MyReviewMemoList;
import com.bookripple.api.domain.review.dto.ReviewMemoResDto.ReviewAndMemo;
import com.bookripple.api.domain.review.entity.Review;
import com.bookripple.api.domain.review.entity.ReviewMemo;
import com.bookripple.api.domain.review.repository.ReviewMemoRepository;
import com.bookripple.api.domain.review.repository.ReviewRepository;
import com.bookripple.api.global.converter.GlobalConverter;
import com.bookripple.api.global.dto.GlobalDto.ContentReq;
import com.bookripple.api.global.dto.GlobalDto.IdRes;
import jakarta.transaction.Transactional;
import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
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

  @Override
  @Transactional
  public IdRes updateReviewMemo(Long reviewMemoId, Long memberId, ContentReq request) {
    ReviewMemo reviewMemo = reviewMemoRepository.findById(reviewMemoId)
        .orElseThrow(() -> new ApiException(ReviewErrorCode.NO_REVIEW_MEMO));

    if (!reviewMemo.getMember().getId().equals(memberId)) {
      throw new ApiException(ReviewErrorCode.MEMO_FORBIDDEN);
    }

    reviewMemo.update(request.content());

    return GlobalConverter.toIdRes(reviewMemoId);
  }

  @Override
  public IdRes deleteReviewMemo(Long reviewMemoId, Long memberId) {
    ReviewMemo reviewMemo = reviewMemoRepository.findById(reviewMemoId)
        .orElseThrow(() -> new ApiException(ReviewErrorCode.NO_REVIEW_MEMO));

    if (!reviewMemo.getMember().getId().equals(memberId)) {
      throw new ApiException(ReviewErrorCode.MEMO_FORBIDDEN);
    }
    reviewMemoRepository.delete(reviewMemo);

    return GlobalConverter.toIdRes(reviewMemoId);
  }

  @Override
  public MyReviewMemoList getMyReviewMemoList(Long memberId, String lastBookTitle,
      Long lastMemoId, int size) {

    Pageable pageable = PageRequest.of(0, size);

    Slice<ReviewMemo> reviewMemoSlice = reviewMemoRepository.findMyReviewMemoByCursor(memberId,
        lastBookTitle, lastMemoId, pageable);

    List<ReviewAndMemo> reviewAndMemoList = reviewMemoSlice.stream()
        .map(ReviewMemoConverter::toReviewAndMemo)
        .toList();

    String nextBookTitle = null;
    Long nextId = null;

    if (!reviewAndMemoList.isEmpty()) {
      ReviewAndMemo last = reviewAndMemoList.get(reviewAndMemoList.size() - 1);
      nextBookTitle = last.bookTitle();
      nextId = last.reviewMemoId();
    }

    return ReviewMemoConverter.toMyReviewMemoList(reviewAndMemoList, nextBookTitle,
        nextId, reviewMemoSlice.hasNext());
  }
}
