package com.bookripple.api.domain.review.service;

import com.bookripple.api.common.code.BookErrorCode;
import com.bookripple.api.common.code.ReviewErrorCode;
import com.bookripple.api.common.error.ApiException;
import com.bookripple.api.domain.book.entity.Book;
import com.bookripple.api.domain.book.repository.BookRepository;
import com.bookripple.api.domain.member.entity.Member;
import com.bookripple.api.domain.member.repository.MemberRepository;
import com.bookripple.api.domain.review.converter.ReviewConverter;
import com.bookripple.api.domain.review.dto.ReviewResDto.Item;
import com.bookripple.api.domain.review.dto.ReviewResDto.MyReview;
import com.bookripple.api.domain.review.dto.ReviewResDto.MyReviewList;
import com.bookripple.api.domain.review.dto.ReviewResDto.ReviewList;
import com.bookripple.api.domain.review.entity.Review;
import com.bookripple.api.domain.review.repository.ReviewRepository;
import com.bookripple.api.global.converter.GlobalConverter;
import com.bookripple.api.global.dto.GlobalDto.ContentReq;
import com.bookripple.api.global.dto.GlobalDto.IdRes;
import jakarta.transaction.Transactional;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class ReviewServiceImpl implements ReviewService {

  private final BookRepository bookRepository;
  private final ReviewRepository reviewRepository;
  private final MemberRepository memberRepository;

  @Override
  @Transactional
  public IdRes createReview(Long bookId, Long memberId, ContentReq request) {

    Book book = bookRepository.findById(bookId)
        .orElseThrow(() -> new ApiException(BookErrorCode.NO_BOOK));

    Member member = memberRepository.getReferenceById(memberId);

    Review review = ReviewConverter.toReview(request, member, book);

    reviewRepository.save(review);
    return GlobalConverter.toIdRes(review.getId());
  }

  @Override
  public IdRes deleteReview(Long reviewId, Long memberId) {
    Review review = reviewRepository.findById(reviewId)
        .orElseThrow(() -> new ApiException(ReviewErrorCode.NO_REVIEW));

    if (!review.getMember().getId().equals(memberId)) {
      throw new ApiException(ReviewErrorCode.FORBIDDEN);
    }
    reviewRepository.delete(review);

    return GlobalConverter.toIdRes(reviewId);
  }

  @Override
  @Transactional
  public IdRes updateReview(Long reviewId, Long memberId, ContentReq request) {
    Review review = reviewRepository.findById(reviewId)
        .orElseThrow(() -> new ApiException(ReviewErrorCode.NO_REVIEW));

    if (!review.getMember().getId().equals(memberId)) {
      throw new ApiException(ReviewErrorCode.FORBIDDEN);
    }
    review.update(request.content());

    return GlobalConverter.toIdRes(reviewId);
  }

  @Override
  public ReviewList getReviews(Long bookId, Long memberId, Long lastId, int size) {
    
    if (!bookRepository.existsById(bookId)) {
      throw new ApiException(BookErrorCode.NO_BOOK);
    }

    Long cursor = (lastId == null) ? Long.MAX_VALUE : lastId;

    Pageable pageable = PageRequest.of(0, size);

    Slice<Review> reviewSlice = reviewRepository.findReviewByCursor(bookId, memberId, cursor,
        pageable);

    List<Item> reviewList = reviewSlice.getContent().stream()
        .map(ReviewConverter::toItem)
        .toList();

    Long nextCursor = null;
    if (!reviewList.isEmpty()) {
      nextCursor = reviewList.get(reviewList.size() - 1).id();
    }
    return ReviewConverter.toReviewList(reviewList, nextCursor, reviewSlice.hasNext());
  }

  @Override
  public MyReviewList getMyReviews(Long memberId, String lastBookTitle, Long lastId, int size) {

    Pageable pageable = PageRequest.of(0, size);
    Slice<Review> reviewSlice = reviewRepository.findMyReviewByCursor(memberId, lastBookTitle
        , lastId, pageable);

    List<MyReview> myReviewList = reviewSlice.getContent().stream()
        .map(ReviewConverter::toMyReview)
        .toList();

    String nextBookTitle = null;
    Long nextId = null;

    if (!myReviewList.isEmpty()) {
      MyReview last = myReviewList.get(myReviewList.size() - 1);
      nextBookTitle = last.bookTitle();
      nextId = last.id();
    }

    return ReviewConverter.toMyReviewList(myReviewList, nextBookTitle, nextId,
        reviewSlice.hasNext());
  }
}
