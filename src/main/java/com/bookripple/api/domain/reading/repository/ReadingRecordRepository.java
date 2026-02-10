package com.bookripple.api.domain.reading.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.bookripple.api.domain.reading.entity.ReadingRecord;

import java.util.Optional;

public interface ReadingRecordRepository extends JpaRepository<ReadingRecord, Long> {

    /**
     * 특정 사용자의 특정 도서의 가장 첫 번째 독서 기록 조회 (독서 시작 날짜 파악 목적)
     */
    @Query("SELECT r FROM ReadingRecord r " +
            "WHERE r.member.id = :memberId " +
            "AND r.book.id = :bookId " +
            "ORDER BY r.createdAt ASC " +
            "LIMIT 1")
    Optional<ReadingRecord> findFirstByMemberIdAndBookIdOrderByCreatedAtAsc(
            @Param("memberId") Long memberId,
            @Param("bookId") Long bookId
    );

    /**
     * 특정 사용자의 특정 도서의 가장 최신 독서 기록 조회 (현재까지 읽은 페이지 파악 목적)
     */
    @Query("SELECT r FROM ReadingRecord r " +
            "WHERE r.member.id = :memberId " +
            "AND r.book.id = :bookId " +
            "ORDER BY r.createdAt DESC " +
            "LIMIT 1")
    Optional<ReadingRecord> findLatestByMemberIdAndBookId(
            @Param("memberId") Long memberId,
            @Param("bookId") Long bookId
    );
}

