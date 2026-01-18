package com.bookripple.api.domain.recommendation.service;

import com.bookripple.api.common.code.BookErrorCode;
import com.bookripple.api.common.code.RecommendErrorCode;
import com.bookripple.api.common.error.ApiException;
import com.bookripple.api.domain.book.entity.Book;
import com.bookripple.api.domain.book.repository.BookRepository;
import com.bookripple.api.domain.member.entity.Member;
import com.bookripple.api.domain.member.repository.MemberRepository;
import com.bookripple.api.domain.recommendation.converter.RecommendConverter;
import com.bookripple.api.domain.recommendation.dto.RecommendReqDto.Create;
import com.bookripple.api.domain.recommendation.entity.Recommendation;
import com.bookripple.api.domain.recommendation.repository.RecommendRepository;
import com.bookripple.api.global.converter.GlobalConverter;
import com.bookripple.api.global.dto.GlobalDto.ContentReq;
import com.bookripple.api.global.dto.GlobalDto.IdRes;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RecommendServiceImpl implements RecommendService {

  private final MemberRepository memberRepository;
  private final BookRepository bookRepository;
  private final RecommendRepository recommendRepository;

  @Override
  @Transactional
  public IdRes createRecommendation(Long memberId, Long sourceBookId, Create request) {

    Member member = memberRepository.getReferenceById(memberId);

    Book sourceBook = bookRepository.findById(sourceBookId)
        .orElseThrow(() -> new ApiException(BookErrorCode.NO_BOOK));

    Book targetBook = bookRepository.findById(request.targetBookId())
        .orElseThrow(() -> new ApiException(BookErrorCode.NO_BOOK));

    Recommendation recommendation = RecommendConverter.toRecommendation(member, sourceBook,
        targetBook, request.content());

    recommendRepository.save(recommendation);

    return GlobalConverter.toIdRes(recommendation.getId());
  }

  @Override
  @Transactional
  public IdRes updateRecommendation(Long memberId, Long recommendationId, ContentReq request) {

    Recommendation recommendation = recommendRepository.findById(recommendationId)
        .orElseThrow(() -> new ApiException(RecommendErrorCode.NO_RECOMMENDATION));

    if (!recommendation.getMember().getId().equals(memberId)) {
      throw new ApiException(RecommendErrorCode.FORBIDDEN);
    }
    recommendation.update(request.content());

    return GlobalConverter.toIdRes(recommendationId);
  }
}
