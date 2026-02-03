package com.bookripple.api.domain.book.converter;

import com.bookripple.api.domain.book.dto.BookLikeRes;

public class BookLikeConverter {

    private BookLikeConverter() {}

    public static BookLikeRes toLikeStatus(Long bookId, boolean liked) {
        return BookLikeRes.builder()
                .bookId(bookId)
                .liked(liked)
                .build();
    }
}
