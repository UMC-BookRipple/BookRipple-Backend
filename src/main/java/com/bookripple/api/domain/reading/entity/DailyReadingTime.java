package com.bookripple.api.domain.reading.entity;

import com.bookripple.api.domain.member.entity.Member;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(
        name = "daily_reading_time",
        indexes = {
                @Index(name = "idx_daily_reading_time_member_date", columnList = "member_id, reading_date"),
                @Index(name = "idx_daily_reading_time_member", columnList = "member_id")
        },
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_daily_reading_time_member_date",
                        columnNames = {"member_id", "reading_date"}
                )
        }
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public class DailyReadingTime {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @Column(name = "reading_date", nullable = false)
    private LocalDate readingDate;

    /**
     * 해당 날짜의 총 독서 시간 (초 단위)
     */
    @Column(name = "total_reading_time", nullable = false)
    private int totalReadingTime;

    @PrePersist
    private void initDefaults() {
        if (this.totalReadingTime < 0) {
            this.totalReadingTime = 0;
        }
    }

    /**
     * 읽기 시간 더하기
     */
    public void addReadingTime(int seconds) {
        if (seconds > 0) {
            this.totalReadingTime += seconds;
        }
    }
}
