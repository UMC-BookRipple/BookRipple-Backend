package com.bookripple.api.domain.reading.repository;

import com.bookripple.api.domain.reading.entity.DailyReadingTime;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface DailyReadingTimeRepository extends JpaRepository<DailyReadingTime, Long> {

    /**
     * 특정 사용자의 특정 날짜 독서 시간 조회
     */
    Optional<DailyReadingTime> findByMemberIdAndReadingDate(Long memberId, LocalDate readingDate);

    /**
     * 특정 사용자의 일주일 독서 시간 조회 (가장 최근 7일)
     */
    @Query("SELECT d FROM DailyReadingTime d " +
            "WHERE d.member.id = :memberId " +
            "AND d.readingDate >= :startDate " +
            "AND d.readingDate <= :endDate " +
            "ORDER BY d.readingDate ASC")
    List<DailyReadingTime> findWeeklyReadingTime(
            @Param("memberId") Long memberId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate
    );
}
