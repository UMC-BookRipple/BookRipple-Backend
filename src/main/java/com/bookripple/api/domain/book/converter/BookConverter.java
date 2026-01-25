package com.bookripple.api.domain.book.converter;

import com.bookripple.api.domain.book.dto.AladinSearchDto;
import com.bookripple.api.domain.book.dto.BookRes;
import com.bookripple.api.domain.book.dto.BookSearchRes;
import com.bookripple.api.domain.book.entity.Book;

import java.util.Collections;
import java.util.List;

public class BookConverter {

    // Book -> BookRes: 단일 도서 조회, 상세 조회 응답
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

    // 알라딘 검색 DTO -> BookSearchRes: 도서 검색 응답
    // 알라딘 연동은 아직이지만 응답 규격만 미리 고정해둠

    public static BookSearchRes toBookSearchRes(AladinSearchDto dto) {

        // 외부 api에서 null 반환 시 에러? 그냥 비우기?
        if (dto == null) return null;

        return BookSearchRes.builder()
                .items(Collections.emptyList())
                .build();
    }


    // 알라딘 검색 결과 리스트 -> BookSearchRes.Item 리스트
    private static List<BookSearchRes.Item> toSearchItems(AladinSearchDto dto) {
        if (dto.getItem() == null) return Collections.emptyList();

        return dto.getItem().stream()
                .map(BookConverter::toSearchItem)
                .toList();
    }

    // 알라딘 검색 단일 결과 -> BookSearchRes.Item
    private static BookSearchRes.Item toSearchItem(AladinSearchDto.Item it) {
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

    private static boolean hasNext(int total, int start, int size) {
        return (start > 0 && size > 0) && (start + size <= total);
    }
}