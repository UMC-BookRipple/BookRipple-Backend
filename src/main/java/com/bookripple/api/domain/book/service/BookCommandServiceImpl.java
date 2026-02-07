package com.bookripple.api.domain.book.service;

import com.bookripple.api.domain.book.converter.BookLikeConverter;
import com.bookripple.api.domain.book.dto.BookLikeRes;
import com.bookripple.api.domain.book.entity.Book;
import com.bookripple.api.domain.book.repository.BookRepository;
import com.bookripple.api.domain.member.entity.Member;
import com.bookripple.api.domain.member.repository.MemberRepository;
import com.bookripple.api.domain.reading.entity.ReadingProgress;
import com.bookripple.api.domain.reading.repository.ReadingProgressRepository;

import com.bookripple.api.domain.reading.repository.ReadingStore;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@AllArgsConstructor
public class BookCommandServiceImpl implements BookCommandService {

    private final BookRepository bookRepository;
    private final MemberRepository memberRepository;
    private final ReadingProgressRepository readingProgressRepository;
    private final ReadingStore store;

    @Override
    @Transactional
    public BookLikeRes likeBook(Long bookId, Long memberId) {

        Member member = memberRepository.getReferenceById(memberId);
        Book book = bookRepository.getReferenceById(bookId);

        ReadingProgress progress = store.getOrCreateProgress(member, book);
        progress.toggleLiked(true);

        return BookLikeConverter.toLikeStatus(bookId, true);
    }


    @Override
    @Transactional
    public BookLikeRes unlikeBook(Long bookId, Long memberId) {

        ReadingProgress progress = store.findProgressOrNull(memberId, bookId);
        if (progress != null) {
            progress.toggleLiked(false);
        }

        return BookLikeConverter.toLikeStatus(bookId, false);
    }
}
