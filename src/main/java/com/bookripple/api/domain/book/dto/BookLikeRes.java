package com.bookripple.api.domain.book.dto;

import lombok.Builder;

@Builder
public record BookLikeRes(
        Long bookId,
        boolean liked
) {
}
