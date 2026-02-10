package com.bookripple.api.domain.reading.repository;

import com.bookripple.api.domain.reading.entity.ReadingProgress;
import java.time.LocalDateTime;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ReadingProgressRepository extends JpaRepository<ReadingProgress, Long> {

  ReadingProgress findByMemberIdAndBookId(Long memberId, Long bookId);

  List<ReadingProgress> findByMemberIdAndIsLikedTrueAndIdLessThanOrderByIdDesc(Long memberId, Long lastId, Pageable pageable);

  List<ReadingProgress> findByMemberIdAndIsLikedTrueOrderByIdDesc(Long memberId, Pageable pageable);

  List<ReadingProgress> findAllByIsCompletedFalseAndUpdatedAtBefore(LocalDateTime threshold);

  List<ReadingProgress> findAllByIsCompletedTrueAndUpdatedAtBefore(LocalDateTime threshold);
}
