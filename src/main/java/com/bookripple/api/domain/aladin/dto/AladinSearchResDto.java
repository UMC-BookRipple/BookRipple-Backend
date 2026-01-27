package com.bookripple.api.domain.aladin.dto;

import lombok.Getter;

import java.util.List;

// 알라딘 도서 검색 API 응답 DTO 우선 만들어두기만 함

@Getter
public class AladinSearchResDto {
    private Integer totalResults;
    private Integer startIndex;
    private Integer itemsPerPage;
    private List<Item> item;

    @Getter
    public static class Item {
        private Long itemId;        // 알라딘 itemId
        private String title;
        private String author;
        private String publisher;
        private String pubDate;     // "yyyy-MM-dd" or "yyyyMMdd"
        private String cover;
        private String isbn10;
        private String isbn13;
    }
}