package com.bookripple.api.domain.book.service;

import com.bookripple.api.domain.book.dto.BookRes;
import com.bookripple.api.domain.book.dto.BookSearchRes;

public interface BookQueryService {

  BookSearchRes searchFromAladin(
      String keyword, int start, int size, String queryType, String searchTarget);

  BookRes getOrCreateByAladinItemId(Long itemId);

  BookSearchRes getSpecialNewBooks();
}
