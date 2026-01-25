package com.bookripple.api.domain.book.service;

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
    //private final AladinClient aladinClient; -> 외부 api 관련한 파일 어디에 둘지 회의때 논의 후 추가할 예정

    @Override
    @Transactional(readOnly = true)
    public BookSearchRes searchFromAladin(String keyword, int start, int size, String queryType, String searchTarget) {
        // Aladin 연동 바로 구현 예정
        return null;
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
