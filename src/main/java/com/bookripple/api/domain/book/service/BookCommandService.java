package com.bookripple.api.domain.book.service;

import com.bookripple.api.domain.book.dto.BookLikeRes;

public interface BookCommandService {

    BookLikeRes like(Long bookId, Long memberId);

    BookLikeRes unlike(Long bookId, Long memberId);
}
