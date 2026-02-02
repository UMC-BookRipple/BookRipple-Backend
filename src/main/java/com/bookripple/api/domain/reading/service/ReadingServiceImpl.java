package com.bookripple.api.domain.reading.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bookripple.api.domain.book.entity.Book;
import com.bookripple.api.domain.book.repository.BookRepository;
import com.bookripple.api.domain.member.entity.Member;
import com.bookripple.api.domain.member.repository.MemberRepository;
import com.bookripple.api.domain.reading.converter.ReadingConverter;
import com.bookripple.api.domain.reading.dto.ReadingDto;
import com.bookripple.api.domain.reading.entity.ReadingProgress;
import com.bookripple.api.domain.reading.entity.ReadingSession;
import com.bookripple.api.domain.reading.repository.ReadingStore;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class ReadingServiceImpl implements ReadingService {

    private final ReadingStore store;
    private final MemberRepository memberRepository;
    private final BookRepository bookRepository;

    @Override
    public ReadingDto.StartRes start(Long memberId, ReadingDto.StartReq req) {
        Long bookId = req.getBookId();

        store.findActiveSession(memberId, bookId).ifPresent(s -> {
            throw new IllegalStateException("ACTIVE_SESSION_ALREADY_EXISTS");
        });

        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("member not found"));
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new IllegalArgumentException("book not found"));

        ReadingSession session = ReadingConverter.toSession(member, book);
        store.saveSession(session);

        store.getOrCreateProgress(member, book);

        return ReadingConverter.toStartRes(session);
    }

    @Override
    public ReadingDto.PauseRes pause(Long memberId, Long sessionId) {
        ReadingSession session = store.findSessionByIdAndMember(sessionId, memberId)
                .orElseThrow(() -> new IllegalArgumentException("session not found"));

        session.pause();

        return ReadingConverter.toPauseRes(session);
    }

    @Override
    public ReadingDto.EndRes end(Long memberId, ReadingDto.EndReq req) {
        ReadingSession session = store.findSessionByIdAndMember(req.getSessionId(), memberId)
                .orElseThrow(() -> new IllegalArgumentException("session not found"));

        int sessionSeconds = session.end();

        var record = ReadingConverter.toRecord(session, sessionSeconds, req.getContent());
        store.saveRecord(record);

        ReadingProgress progress = store.getOrCreateProgress(session.getMember(), session.getBook());
        progress.addReadingTime(sessionSeconds);

        return ReadingConverter.toEndRes(record, sessionSeconds, progress);
    }

    @Override
    public ReadingDto.CompleteRes complete(Long memberId, ReadingDto.CompleteReq req) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("member not found"));
        Book book = bookRepository.findById(req.getBookId())
                .orElseThrow(() -> new IllegalArgumentException("book not found"));

        ReadingProgress progress = store.getOrCreateProgress(member, book);
        progress.markCompleted();

        return ReadingConverter.toCompleteRes(book.getId(), progress);
    }
}
