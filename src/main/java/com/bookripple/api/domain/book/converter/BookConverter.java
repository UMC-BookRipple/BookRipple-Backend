package com.bookripple.api.domain.book.converter;

import com.bookripple.api.domain.aladin.dto.AladinSearchResDto;
import com.bookripple.api.domain.book.dto.BookRes;
import com.bookripple.api.domain.book.dto.BookSearchRes;
import com.bookripple.api.domain.book.entity.Book;

import java.util.Collections;
import java.util.List;

public class BookConverter {

    public static BookRes toBookRes(Book book) {
        return BookRes.builder()
                .bookId(book.getId())
                .aladinItemId(book.getAladinBookId())
                .title(book.getTitle())
                .author(book.getAuthor())
                .publisher(book.getPublisher())
                .coverUrl(book.getBookCover())
                .isbn10(book.getIsbn10())
                .isbn13(book.getIsbn13())
                .publishedAt(book.getPublishedAt())
                .totalPage(book.getTotalPage())
                .story(book.getStory())
                .build();
    }

    // 알라딘 검색 DTO -> BookSearchRes
    public static BookSearchRes toBookSearchRes(AladinSearchResDto dto) {
        if (dto == null) {
            // null이면 빈 응답으로 안전하게 반환 (외부 API 불안정 대응)
            return BookSearchRes.builder()
                    .totalResults(0)
                    .startIndex(0)
                    .itemsPerPage(0)
                    .hasNext(false)
                    .items(Collections.emptyList())
                    .build();
        }

        int total = dto.getTotalResults() == null ? 0 : dto.getTotalResults();
        int startIndex = dto.getStartIndex() == null ? 0 : dto.getStartIndex();
        int itemsPerPage = dto.getItemsPerPage() == null ? 0 : dto.getItemsPerPage();

        List<BookSearchRes.Item> items = toSearchItems(dto);
        boolean hasNext = hasNext(total, startIndex, itemsPerPage);

        return BookSearchRes.builder()
                .totalResults(total)
                .startIndex(startIndex)
                .itemsPerPage(itemsPerPage)
                .hasNext(hasNext)
                .items(items)
                .build();
    }

    private static List<BookSearchRes.Item> toSearchItems(AladinSearchResDto dto) {
        if (dto.getItem() == null) return Collections.emptyList();

        return dto.getItem().stream()
                .map(BookConverter::toSearchItem)
                .toList();
    }

    private static BookSearchRes.Item toSearchItem(AladinSearchResDto.Item it) {
        return BookSearchRes.Item.builder()
                .aladinItemId(it.getItemId())
                .title(it.getTitle())
                .author(it.getAuthor())
                .publisher(it.getPublisher())
                .coverUrl(it.getCover())
                .pubDate(it.getPubDate())
                .isbn10(it.getIsbn10())
                .isbn13(it.getIsbn13())
                .build();
    }

    // startIndex는 "현재 페이지의 첫 번째 인덱스(1 기반)"로 들어오는 경우가 많아서
    // startIndex + itemsPerPage - 1 < totalResults 이면 다음 페이지 있음
    private static boolean hasNext(int total, int startIndex, int itemsPerPage) {
        if (total <= 0 || startIndex <= 0 || itemsPerPage <= 0) return false;
        return (startIndex + itemsPerPage - 1) < total;
    }
}
