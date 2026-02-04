package com.bookripple.api.domain.book.repository;

import com.bookripple.api.domain.reading.entity.ReadingProgress;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BookLikeRepository extends JpaRepository<ReadingProgress, Long> {

    boolean existsByBookIdAndMemberId(Long bookId, Long memberId);

    void deleteByBookIdAndMemberId(Long bookId, Long memberId);
}

