package com.bookripple.api.domain.book.service;

import com.bookripple.api.common.code.BookErrorCode;
import com.bookripple.api.common.code.MemberErrorCode;
import com.bookripple.api.common.code.SearchErrorCode;
import com.bookripple.api.common.error.ApiException;
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
import java.util.List;
import java.util.Set;

import com.bookripple.api.domain.library.enums.LibraryStatus;
import com.bookripple.api.domain.library.repository.LibraryItemRepository;
import com.bookripple.api.domain.member.repository.MemberRepository;
import com.bookripple.api.domain.reading.repository.ReadingProgressRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class BookQueryServiceImpl implements BookQueryService {

  private final BookRepository bookRepository;
  private final AladinService aladinService;
  private final SearchHistoryCommandService searchHistoryCommandService;
  private final LibraryItemRepository libraryItemRepository;
  private final MemberRepository memberRepository;
  private final ReadingProgressRepository readingProgressRepository;



  //1 알라딘 검색
  @Override
  @Transactional
  public BookSearchRes searchFromAladin(Long memberId, String keyword, int start, int size, String queryType,
      String searchTarget, SearchLogType type) {
    // 최소 검증
    if (keyword == null || keyword.isBlank()) {
      throw new ApiException(SearchErrorCode.NO_SEARCH_KEYWORD);
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

    Set<Long> registeredAladinIds = java.util.Collections.emptySet();

    if (memberId != null && resDto != null && resDto.getItem() != null && !resDto.getItem().isEmpty()) {
      List<Long> aladinIds = resDto.getItem().stream()
              .map(AladinSearchResDto.Item::getItemId)
              .filter(id -> id != null)
              .toList();

      List<LibraryStatus> status = List.of(
              LibraryStatus.COMPLETED,
              LibraryStatus.READING,
              LibraryStatus.LIKED
      );

      registeredAladinIds = new java.util.HashSet<>(
              libraryItemRepository.findRegisteredAladinBookIds(memberId, status, aladinIds)
      );
    }

    if (memberId != null) {
      String normalized = keyword.trim();
      if (!normalized.isBlank()) {
        searchHistoryCommandService.saveOrRefresh(memberId, normalized, type);
      }
    }


    return BookConverter.toBookSearchRes(resDto, registeredAladinIds);
  }

    // 추천도서용 알라딘 도서 상세 조회 및 저장
  @Override
  @Transactional
  public BookRes getOrCreateByAladinItemId(Long itemId) {
    if (itemId == null) throw new ApiException(BookErrorCode.NO_BOOK);

    return bookRepository.findByAladinBookId(itemId)
            .map(BookConverter::toBookRes)
            .orElseGet(() -> BookConverter.toBookRes(createFromAladinEntity(itemId)));

  }


  //2. 알라딘 도서 상세 조회 및 저장
  @Override
  @Transactional
  public BookRes getOrCreateByAladinItemId(Long memberId, Long itemId) {
    if (itemId == null) throw new ApiException(BookErrorCode.NO_BOOK);
    if (memberId == null) throw new ApiException(MemberErrorCode.MEMBER_NOT_FOUND);

    // 1) Book 캐시 히트 or 생성
    Book book = bookRepository.findByAladinBookId(itemId)
            .orElseGet(() -> createFromAladinEntity(itemId));

    // 2) 🔥 등록 처리 (항상 실행)
    upsertLibraryItemReading(memberId, book);
    resetReadingProgress(memberId, book);

    // 3) 응답
    return BookConverter.toBookRes(book);
  }



  private Book createFromAladinEntity(Long itemId) {
    AladinItemLookUpResDto lookUp = aladinService.lookup(itemId, null);

    AladinItemLookUpResDto.Item it =
            (lookUp == null || lookUp.getItem() == null || lookUp.getItem().isEmpty())
                    ? null
                    : lookUp.getItem().get(0);

    if (it == null) {
      throw new ApiException(BookErrorCode.NO_BOOK);
    }

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

    return bookRepository.save(book);
  }


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

  private void upsertLibraryItemReading(Long memberId, Book book) {
    var itemOpt = libraryItemRepository.findByMemberIdAndBook_Id(memberId, book.getId());

    if (itemOpt.isPresent()) {
      itemOpt.get().setStatus(LibraryStatus.READING);
    } else {
      libraryItemRepository.save(
              com.bookripple.api.domain.library.entity.LibraryItem.builder()
                      .member(memberRepository.getReferenceById(memberId))
                      .book(book)
                      .status(LibraryStatus.READING)
                      .build()
      );
    }
  }

  private void resetReadingProgress(Long memberId, Book book) {
    var progress = readingProgressRepository
            .findByMemberIdAndBookId(memberId, book.getId());

    if (progress == null) {
      readingProgressRepository.save(
              com.bookripple.api.domain.reading.entity.ReadingProgress.builder()
                      .member(memberRepository.getReferenceById(memberId))
                      .book(book)
                      .readingTime(0)
                      .progress(java.math.BigDecimal.ZERO)
                      .isLiked(false)
                      .isCompleted(false)
                      .build()
      );
    } else {
      progress.setReadingTime(0);
      progress.setProgress(java.math.BigDecimal.ZERO);
      progress.setLiked(false);
      progress.setCompleted(false);
    }
  }

}
