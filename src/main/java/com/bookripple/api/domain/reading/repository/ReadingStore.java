package com.bookripple.api.domain.reading.repository;

import java.util.Optional;

import com.bookripple.api.domain.reading.enums.ReadingSessionStatus;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.bookripple.api.domain.book.entity.Book;
import com.bookripple.api.domain.member.entity.Member;
import com.bookripple.api.domain.reading.entity.*;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReadingStore {

    private final ReadingSessionRepository sessionRepository;
    private final ReadingProgressRepository progressRepository;
    private final ReadingRecordRepository recordRepository;

    public Optional<ReadingSession> findSessionByIdAndMember(Long sessionId, Long memberId) {
        return sessionRepository.findByIdAndMemberId(sessionId, memberId);
    }

    public Optional<ReadingSession> findActiveSession(Long memberId, Long bookId) {
        return sessionRepository.findByMemberIdAndBookIdAndStatus(memberId, bookId, ReadingSessionStatus.ACTIVE);
    }

    @Transactional
    public ReadingSession saveSession(ReadingSession session) {
        return sessionRepository.save(session);
    }

    public ReadingProgress findProgressOrNull(Long memberId, Long bookId) {
        return progressRepository.findByMemberIdAndBookId(memberId, bookId);
    }

    public Optional<ReadingSession> findPausedSession(Long memberId, Long bookId) {
        return sessionRepository.findByMemberIdAndBookIdAndStatus(memberId, bookId, ReadingSessionStatus.PAUSED);
    }

    @Transactional
    public ReadingProgress saveProgress(ReadingProgress progress) {
        return progressRepository.save(progress);
    }

    @Transactional
    public ReadingProgress getOrCreateProgress(Member member, Book book) {
        ReadingProgress progress = progressRepository.findByMemberIdAndBookId(member.getId(), book.getId());
        if (progress != null) {
            return progress;
        }

        // 없으면 생성
        try {
            return progressRepository.save(
                    ReadingProgress.builder()
                            .member(member)
                            .book(book)
                            .build()
            );
        } catch (org.springframework.dao.DataIntegrityViolationException e) {
            // 동시성으로 누가 먼저 만들었을 수 있음 -> 다시 조회
            ReadingProgress retry = progressRepository.findByMemberIdAndBookId(member.getId(), book.getId());
            if (retry != null) return retry;
            throw e;
        }
    }

    @Transactional
    public ReadingRecord saveRecord(ReadingRecord record) {
        return recordRepository.save(record);
    }
}
