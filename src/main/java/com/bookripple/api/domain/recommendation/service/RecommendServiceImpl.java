package com.bookripple.api.domain.recommendation.service;

import com.bookripple.api.common.code.BookErrorCode;
import com.bookripple.api.common.code.CommonErrorCode;
import com.bookripple.api.common.code.RecommendErrorCode;
import com.bookripple.api.common.error.ApiException;
import com.bookripple.api.domain.book.entity.Book;
import com.bookripple.api.domain.book.repository.BookRepository;
import com.bookripple.api.domain.member.entity.Member;
import com.bookripple.api.domain.member.repository.MemberRepository;
import com.bookripple.api.domain.recommendation.converter.RecommendConverter;
import com.bookripple.api.domain.recommendation.dto.RecommendReqDto.Create;
import com.bookripple.api.domain.recommendation.dto.RecommendResDto.MyRecommend;
import com.bookripple.api.domain.recommendation.dto.RecommendResDto.MyRecommendList;
import com.bookripple.api.domain.recommendation.dto.RecommendResDto.Recommend;
import com.bookripple.api.domain.recommendation.dto.RecommendResDto.RecommendList;
import com.bookripple.api.domain.recommendation.entity.Recommendation;
import com.bookripple.api.domain.recommendation.repository.RecommendRepository;
import com.bookripple.api.global.converter.GlobalConverter;
import com.bookripple.api.global.dto.GlobalDto.ContentReq;
import com.bookripple.api.global.dto.GlobalDto.IdList;
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

  @Override
  public IdRes deleteRecommendation(Long memberId, Long recommendationId) {

    Recommendation recommendation = recommendRepository.findById(recommendationId)
        .orElseThrow(() -> new ApiException(RecommendErrorCode.NO_RECOMMENDATION));

    if (!recommendation.getMember().getId().equals(memberId)) {
      throw new ApiException(RecommendErrorCode.FORBIDDEN);
    }

    recommendRepository.delete(recommendation);

    return GlobalConverter.toIdRes(recommendationId);
  }

  @Override
  public RecommendList getRecommendList(Long memberId, Long bookId, Long lastId, int size) {

    if (!bookRepository.existsById(bookId)) {
      throw new ApiException(BookErrorCode.NO_BOOK);
    }

    Long cursor = (lastId == null) ? Long.MAX_VALUE : lastId;

    Pageable pageable = PageRequest.of(0, size);

    Slice<Recommendation> recommendSlice = recommendRepository.findRecommendationByCursor(bookId,
        memberId, cursor,
        pageable);

    List<Recommend> recommendList = recommendSlice.stream()
        .map(RecommendConverter::toRecommend)
        .toList();

    Long nextCursor = null;

    if (!recommendList.isEmpty()) {
      nextCursor = recommendList.get(recommendList.size() - 1).id();
    }
    return RecommendConverter.toRecommendList(recommendList, nextCursor, recommendSlice.hasNext());
  }

  @Override
  public MyRecommendList getMyRecommendList(Long memberId, String lastBookTitle, Long lastId,
      int size) {

    Pageable pageable = PageRequest.of(0, size);

    Slice<Recommendation> myRecommendSlice = recommendRepository.findMyRecommendationByCursor(
        memberId, lastBookTitle, lastId, pageable);

    List<MyRecommend> myRecommendList = myRecommendSlice.stream()
        .map(RecommendConverter::toMyRecommend)
        .toList();

    String nextBookTitle = null;
    Long nextId = null;

    if (!myRecommendList.isEmpty()) {
      MyRecommend last = myRecommendList.get(myRecommendList.size() - 1);
      nextBookTitle = last.sourceBookTitle();
      nextId = last.id();
    }
    return RecommendConverter.toMyRecommendList(myRecommendList, nextBookTitle, nextId,
        myRecommendSlice.hasNext());
  }

  @Override
  @Transactional
  public void deleteMyRecommendations(Long memberId, IdList request) {
    if (request.idList() == null || request.idList().isEmpty()) {
      throw new ApiException(CommonErrorCode.BAD_REQUEST);
    }
    recommendRepository.deleteMyRecommendations(memberId, request.idList());
  }
}
