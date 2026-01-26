package com.bookripple.api.domain.book.dto;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;

// 도서 상세 응답 DTO
@Getter
@Builder
public class BookRes {
    private Long bookId;         // 북리플 내 DB pk
    private Long aladinItemId;   // 알라딘 pk
    private String title;
    private String author;
    private String publisher;
    private String coverUrl;
    private String isbn10;
    private String isbn13;
    private LocalDate publishedAt;
    private Integer totalPage;
    private String story;        // 알라딘 description or null
}
