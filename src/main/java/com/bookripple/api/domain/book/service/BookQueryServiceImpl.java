package com.bookripple.api.domain.book.service;

import com.bookripple.api.domain.aladin.client.AladinClient;
import com.bookripple.api.domain.aladin.dto.AladinSearchResDto;
import com.bookripple.api.domain.book.converter.BookConverter;
import com.bookripple.api.domain.book.dto.BookRes;
import com.bookripple.api.domain.book.dto.BookSearchRes;
import com.bookripple.api.domain.book.repository.BookRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class BookQueryServiceImpl implements BookQueryService {

    private final BookRepository bookRepository;
    private final AladinClient aladinClient;

    @Override
    @Transactional(readOnly = true)
    public BookSearchRes searchFromAladin(String keyword, int start, int size, String queryType, String searchTarget) {
        // 최소 검증(Controller에서도 하지만, 서비스 단에서도 방어)
        if (keyword == null || keyword.isBlank()) {
            throw new IllegalArgumentException("keyword must not be blank");
        }
        if (start < 1) start = 1;
        if (size < 1) size = 10;
        if (size > 50) size = 50;

        AladinSearchResDto resDto = aladinClient.search(keyword, start, size, queryType, searchTarget);
        return BookConverter.toBookSearchRes(resDto);
    }

    @Override
    @Transactional
    public BookRes getOrCreateByAladinItemId(Long itemId) {
        return null;
        // Aladin 도서 상세 조회는 외부 API 연동 후 구현 예정
        // isbn13 또는 aladinBookId로 Book 조회 후 없으면 Aladin API 통해 도서 정보 받아와서 저장하는 방식 쓸 예정
        // 왜냐면 알라딘 쿼리 5000 제한이 있음...
    }
}
