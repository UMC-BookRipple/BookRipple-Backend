package com.bookripple.api.domain.review.service;

import com.bookripple.api.domain.book.entity.Book;
import com.bookripple.api.domain.book.exception.BookException;
import com.bookripple.api.domain.book.exception.code.BookErrorCode;
import com.bookripple.api.domain.book.repository.BookRepository;
import com.bookripple.api.domain.member.entity.Member;
import com.bookripple.api.domain.member.exception.MemberException;
import com.bookripple.api.domain.member.exception.code.MemberErrorCode;
import com.bookripple.api.domain.member.repository.MemberRepository;
import com.bookripple.api.domain.review.converter.ReviewConverter;
import com.bookripple.api.domain.review.entity.Review;
import com.bookripple.api.domain.review.repository.ReviewRepository;
import com.bookripple.api.global.converter.GlobalConverter;
import com.bookripple.api.global.dto.GlobalDto.ContentReq;
import com.bookripple.api.global.dto.GlobalDto.IdRes;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
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
        .orElseThrow(() -> new BookException(BookErrorCode.NO_BOOK));
    Member member = memberRepository.findById(memberId)
        .orElseThrow(() -> new MemberException(MemberErrorCode.NO_MEMBER));

    Review review = ReviewConverter.toReview(request, member, book);

    reviewRepository.save(review);
    return GlobalConverter.toIdRes(review.getId());
  }
}
