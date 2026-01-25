package com.bookripple.api.domain.book.dto;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

//검색 응답 DTO
@Getter
@Builder
public class BookSearchRes {
    private int totalResults;
    private int startIndex;
    private int itemsPerPage;
    private boolean hasNext;

    private List<Item> items;

    @Getter
    @Builder
    public static class Item {
        private Long aladinItemId; // 검색기록/상세조회 연결 키
        private String title;
        private String author;     // 여러명일 경우가 있어 일단 문자열로
        private String publisher;
        private String coverUrl;
        private String isbn10;
        private String isbn13;
        private String publishedAt; // 우선 string
    }
}
