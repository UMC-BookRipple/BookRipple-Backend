package com.bookripple.api.domain.book.service;

import com.bookripple.api.domain.aladin.dto.AladinItemLookUpResDto;
import com.bookripple.api.domain.aladin.dto.AladinSearchResDto;
import com.bookripple.api.domain.aladin.service.AladinService;
import com.bookripple.api.domain.book.converter.BookConverter;
import com.bookripple.api.domain.book.dto.BookRes;
import com.bookripple.api.domain.book.dto.BookSearchRes;
import com.bookripple.api.domain.book.entity.Book;
import com.bookripple.api.domain.book.enums.SearchLogType;
import com.bookripple.api.domain.book.repository.BookRepository;
import java.time.LocalDate;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class BookQueryServiceImpl implements BookQueryService {

  private final BookRepository bookRepository;
  private final AladinService aladinService;
  private final SearchHistoryCommandService searchHistoryCommandService;


  //1 알라딘 검색
  @Override
  @Transactional
  public BookSearchRes searchFromAladin(Long memberId, String keyword, int start, int size, String queryType,
      String searchTarget, SearchLogType type) {
    // 최소 검증
    if (keyword == null || keyword.isBlank()) {
      throw new IllegalArgumentException("검색어를 입력하세요");
    }
    if (start < 1) {
      start = 1;
    }
    if (size < 1) {
      size = 10;
    }
    if (size > 50) {
      size = 50;
    }

    AladinSearchResDto resDto =
            aladinService.search(keyword, start, size, queryType, searchTarget);

    if (memberId != null) {
      String normalized = keyword.trim();
      if (!normalized.isBlank()) {
        searchHistoryCommandService.saveOrRefresh(memberId, normalized, type);
      }
    }
      return BookConverter.toBookSearchRes(resDto);
  }

  //2. 알라딘 도서 상세 조회 및 저장
  @Override
  @Transactional
  public BookRes getOrCreateByAladinItemId(Long itemId) {
    if (itemId == null) {
      throw new IllegalArgumentException("도서가 존재하지 않습니다");
    }

    // 1) 캐시 히트
    return bookRepository.findByAladinBookId(itemId)
        .map(BookConverter::toBookRes)
        .orElseGet(() -> createFromAladin(itemId));
  }

  private BookRes createFromAladin(Long itemId) {
    AladinItemLookUpResDto lookUp = aladinService.lookup(itemId, null);

    AladinItemLookUpResDto.Item it =
        (lookUp == null || lookUp.getItem() == null || lookUp.getItem().isEmpty())
            ? null
            : lookUp.getItem().get(0);

    if (it == null) {
      throw new IllegalArgumentException("도서가 존재하지 않습니다");
    }

    // (선택) isbn13로 중복 탐지하고 싶으면 여기서 findByIsbn13 추가
    // 지금은 “없으면 저장”만 한다고 했으니 생략 가능

    Book book = Book.builder()
        .aladinBookId(it.getItemId())
        .title(it.getTitle())
        .author(it.getAuthor())
        .publisher(it.getPublisher())
        .bookCover(it.getCover())
        .isbn10(it.getIsbn10())
        .isbn13(it.getIsbn13())
        .publishedAt(parsePublishedAt(it.getPubDate()))
        .totalPage(it.getSubInfo() != null ? it.getSubInfo().getItemPage() : null)
        .story(it.getDescription())
        .build();

    Book saved = bookRepository.save(book);
    return BookConverter.toBookRes(saved);
  }

  // "yyyy-MM-dd" 형식의 문자열을 LocalDate로 변환
  private LocalDate parsePublishedAt(String pubDate) {
    if (pubDate == null || pubDate.isBlank()) {
      return null;
    }
    return LocalDate.parse(pubDate); // yyyy-MM-dd
  }

  @Override
  public BookSearchRes getSpecialNewBooks() {
    AladinSearchResDto dto = aladinService.getSpecialNewBooks();
    return BookConverter.toSpecialNewBooks(dto);
  }
}
