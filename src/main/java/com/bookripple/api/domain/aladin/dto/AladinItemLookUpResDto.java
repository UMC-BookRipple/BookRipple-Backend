package com.bookripple.api.domain.aladin.dto;

import lombok.Getter;

import java.util.List;

@Getter
public class AladinItemLookUpResDto {
    private List<Item> item;

    @Getter
    public static class Item {
        private Long itemId;
        private String title;
        private String author;
        private String publisher;
        private String pubDate;
        private String cover;
        private String isbn10;
        private String isbn13;

        private String description; // story

        private SubInfo subInfo;    // pageCount

        @Getter
        public static class SubInfo {
            private Integer itemPage; // totalPage 후보 (알라딘 필드명 케이스 확인 필요)
        }
    }
}
