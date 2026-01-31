package com.bookripple.api.domain.library.dto;

import java.util.List;

import lombok.Builder;

@Builder
public record LibraryItemListRes(
        List<LibraryItemRes> items,
        boolean hasNext,
        Long lastId
) {
    public static LibraryItemListRes of(List<LibraryItemRes> items, boolean hasNext, Long lastId) {
        return LibraryItemListRes.builder()
                .items(items)
                .hasNext(hasNext)
                .lastId(lastId)
                .build();
    }
}
