package com.bookripple.api.domain.book.dto;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class BookSearchRes {

    private Integer totalResults;
    private Integer startIndex;
    private Integer itemsPerPage;
    private Boolean hasNext;

    private List<Item> items;

    @Getter
    @Builder
    public static class Item {
        private Long aladinItemId;
        private Boolean registered;
        private String title;
        private String author;
        private String publisher;
        private String coverUrl;
        private String pubDate;
        private String isbn10;
        private String isbn13;
    }
}