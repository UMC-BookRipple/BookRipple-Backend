package com.bookripple.api.domain.reading.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.bookripple.api.domain.reading.entity.ReadingProgress;

public interface ReadingProgressRepository extends JpaRepository<ReadingProgress, Long> {
    Optional<ReadingProgress> findByMemberIdAndBookId(Long memberId, Long bookId);
}
