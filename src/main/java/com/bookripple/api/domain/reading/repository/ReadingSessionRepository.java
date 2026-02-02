package com.bookripple.api.domain.reading.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.bookripple.api.domain.reading.entity.ReadingSession;
import com.bookripple.api.domain.reading.enums.ReadingSessionStatus;

public interface ReadingSessionRepository extends JpaRepository<ReadingSession, Long> {
    Optional<ReadingSession> findByIdAndMemberId(Long sessionId, Long memberId);

    Optional<ReadingSession> findByMemberIdAndBookIdAndStatus(
            Long memberId, Long bookId, ReadingSessionStatus status
    );
}
