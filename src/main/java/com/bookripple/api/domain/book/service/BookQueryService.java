package com.bookripple.api.domain.book.service;

import com.bookripple.api.domain.book.dto.BookRes;
import com.bookripple.api.domain.book.dto.BookSearchRes;
import com.bookripple.api.domain.book.enums.SearchLogType;

public interface BookQueryService {

  BookSearchRes searchFromAladin(
      Long memberId, String keyword, int start, int size, String queryType, String searchTarget, SearchLogType type);

  BookRes getOrCreateByAladinItemId(Long itemId);
  BookRes getOrCreateByAladinItemId(Long memberId, Long itemId);


  BookSearchRes getSpecialNewBooks();
}
