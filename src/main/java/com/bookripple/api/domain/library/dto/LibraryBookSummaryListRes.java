package com.bookripple.api.domain.library.dto;

import lombok.Builder;

import java.util.List;

/**
 * 마이페이지에서 여러 책의 요약 정보 목록을 반환하기 위한 DTO
 */
@Builder
public record LibraryBookSummaryListRes(
        List<LibraryBookSummaryRes> books,
        int totalCount
) {
    public static LibraryBookSummaryListRes of(List<LibraryBookSummaryRes> books) {
        return LibraryBookSummaryListRes.builder()
                .books(books)
                .totalCount(books.size())
                .build();
    }
}
