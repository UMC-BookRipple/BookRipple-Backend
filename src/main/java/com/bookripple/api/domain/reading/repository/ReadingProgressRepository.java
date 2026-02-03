package com.bookripple.api.domain.reading.repository;

import com.bookripple.api.domain.reading.entity.ReadingProgress;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ReadingProgressRepository extends JpaRepository<ReadingProgress, Long> {

  ReadingProgress findByMemberIdAndBookId(Long memberId, Long bookId);
}
