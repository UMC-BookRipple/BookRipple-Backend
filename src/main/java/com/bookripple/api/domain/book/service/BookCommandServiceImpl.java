package com.bookripple.api.domain.book.service;

import com.bookripple.api.common.code.BookErrorCode;
import com.bookripple.api.common.error.ApiException;
import com.bookripple.api.domain.book.converter.BookLikeConverter;
import com.bookripple.api.domain.book.dto.BookLikeResDto.LikeStatus;
import com.bookripple.api.domain.book.entity.Book;
import com.bookripple.api.domain.book.repository.BookRepository;
import com.bookripple.api.domain.member.entity.Member;
import com.bookripple.api.domain.member.repository.MemberRepository;
import com.bookripple.api.domain.reading.entity.ReadingProgress;
import com.bookripple.api.domain.reading.repository.ReadingProgressRepository;
import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@AllArgsConstructor
public class BookCommandServiceImpl implements BookCommandService {

    private final BookRepository bookRepository;
    private final MemberRepository memberRepository;
    private final ReadingProgressRepository readingProgressRepository;

    @Override
    @Transactional
    public LikeStatus likeBook(Long bookId, Long memberId) {
        if (!bookRepository.existsById(bookId)) {
            throw new ApiException(BookErrorCode.NO_BOOK);
        }

        Member member = memberRepository.getReferenceById(memberId);
        Book book = bookRepository.getReferenceById(bookId);

        ReadingProgress progress = readingProgressRepository.findByMemberIdAndBookId(memberId, bookId)
                .orElseGet(() -> createDefaultProgress(member, book));

        // 멱등: 이미 liked=true면 그냥 유지
        progress.toggleLiked(true);

        return BookLikeConverter.toLikeStatus(bookId, true);
    }

    @Override
    @Transactional
    public LikeStatus unlikeBook(Long bookId, Long memberId) {
        if (!bookRepository.existsById(bookId)) {
            throw new ApiException(BookErrorCode.NO_BOOK);
        }

        readingProgressRepository.findByMemberIdAndBookId(memberId, bookId)
                .ifPresent(progress -> progress.toggleLiked(false)); // 멱등: 없으면 그냥 성공

        return BookLikeConverter.toLikeStatus(bookId, false);
    }

    private ReadingProgress createDefaultProgress(Member member, Book book) {
        ReadingProgress created = ReadingProgress.builder()
                .member(member)
                .book(book)
                .currentPage(0)
                .readingTime(0)
                .progress(BigDecimal.ZERO.setScale(2))
                .isLiked(false)
                .isCompleted(false)
                .build();

        try {
            return readingProgressRepository.save(created);
        } catch (DataIntegrityViolationException e) {
            return readingProgressRepository.findByMemberIdAndBookId(member.getId(), book.getId())
                    .orElseThrow(() -> e);
        }
    }
}
