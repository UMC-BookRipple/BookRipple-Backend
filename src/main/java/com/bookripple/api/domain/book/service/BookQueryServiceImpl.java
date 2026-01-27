package com.bookripple.api.domain.book.service;

import com.bookripple.api.domain.aladin.client.AladinClient;
import com.bookripple.api.domain.aladin.dto.AladinItemLookUpResDto;
import com.bookripple.api.domain.aladin.dto.AladinSearchResDto;
import com.bookripple.api.domain.book.converter.BookConverter;
import com.bookripple.api.domain.book.dto.BookRes;
import com.bookripple.api.domain.book.dto.BookSearchRes;
import com.bookripple.api.domain.book.entity.Book;
import com.bookripple.api.domain.book.repository.BookRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class BookQueryServiceImpl implements BookQueryService {

    private final BookRepository bookRepository;
    private final AladinClient aladinClient;

    @Override
    @Transactional(readOnly = true)
    public BookSearchRes searchFromAladin(String keyword, int start, int size, String queryType, String searchTarget) {
        // 최소 검증
        if (keyword == null || keyword.isBlank()) {
            throw new IllegalArgumentException("keyword must not be blank");
        }
        if (start < 1) start = 1;
        if (size < 1) size = 10;
        if (size > 50) size = 50;

        AladinSearchResDto resDto = aladinClient.search(keyword, start, size, queryType, searchTarget);
        return BookConverter.toBookSearchRes(resDto);
    }

    @Override
    @Transactional
    public BookRes getOrCreateByAladinItemId(Long itemId) {
        return null;
        // isbn13 또는 aladinBookId로 Book 조회 후 없으면 Aladin API 통해 도서 정보 받아와서 저장하는 방식 씀
        // 왜냐면 알라딘 쿼리 5000 제한이 있음...

        if (itemId == null) throw new IllegalArgumentException("aladinItemId must not be null");

        // 1) 캐시 히트
        Book cached = bookRepository.findByAladinBookId(itemId).orElse(null);
        if (cached != null) {
            return BookConverter.toBookRes(cached);
        }

        // 2) 알라딘 lookup
        AladinItemLookUpResDto lookUp = aladinClient.lookup(itemId);
        // lookUp 응답 구조에 따라 item 한 건 꺼내야 함
        AladinItemLookUpResDto.Item it = (lookUp == null || lookUp.getItem() == null || lookUp.getItem().isEmpty())
                ? null
                : lookUp.getItem().get(0);

        if (it == null) {
            throw new IllegalStateException("Aladin lookup returned empty result");
        }

        // 3) Book 생성/저장 (1차: 갱신 정책 없이 없으면 저장)
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

    private LocalDate parsePublishedAt(String pubDate) {
        if (pubDate == null || pubDate.isBlank()) return null;
        try {
            if (pubDate.contains("-")) {
                return LocalDate.parse(pubDate); // yyyy-MM-dd
            }
            if (pubDate.length() == 8) {
                int y = Integer.parseInt(pubDate.substring(0, 4));
                int m = Integer.parseInt(pubDate.substring(4, 6));
                int d = Integer.parseInt(pubDate.substring(6, 8));
                return LocalDate.of(y, m, d);
            }
        } catch (Exception ignored) {}
        return null;
    }
}
