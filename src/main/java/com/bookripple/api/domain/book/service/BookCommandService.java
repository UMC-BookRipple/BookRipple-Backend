package com.bookripple.api.domain.book.service;

import com.bookripple.api.domain.book.dto.BookLikeRes;

public interface BookCommandService {

    BookLikeRes likeBook(Long bookId, Long memberId);

    BookLikeRes unlikeBook(Long bookId, Long memberId);
}
