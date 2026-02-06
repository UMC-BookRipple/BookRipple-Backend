package com.bookripple.api.domain.book.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

public class SearchHistoryRes {

    @Getter
    @AllArgsConstructor
    public static class Item {
        private Long historyId;
        private String keyword;
        private LocalDateTime searchedAt;
    }

    @Getter
    @AllArgsConstructor
    public static class ListRes {
        private List<Item> items;
        private Long lastId;
        private boolean hasNext;
    }

    @Getter
    @AllArgsConstructor
    public static class DeleteOne {
        private boolean deleted;
    }

    @Getter
    @AllArgsConstructor
    public static class DeleteAll {
        private long deletedCount;
    }
}

