package com.bookripple.api.domain.reading.entity;

import java.time.LocalDateTime;

import com.bookripple.api.domain.book.entity.Book;
import com.bookripple.api.domain.member.entity.Member;
import com.bookripple.api.domain.reading.enums.ReadingSessionStatus;
import com.bookripple.api.global.entity.BaseEntity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(
        name = "reading_session",
        indexes = {
                @Index(name = "idx_reading_session_member", columnList = "member_id"),
                @Index(name = "idx_reading_session_book", columnList = "book_id")
        }
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public class ReadingSession extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** ACTIVE / PAUSED / ENDED */
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private ReadingSessionStatus status;

    /** 누적 독서 시간 (초) */
    @Column(name = "accumulated_time", nullable = false)
    private int accumulatedTime;

    @Column(name = "last_resumed_at", nullable = false)
    private LocalDateTime lastResumedAt;

    @Column(name = "ended_at")
    private LocalDateTime endedAt;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "book_id", nullable = false)
    private Book book;

    @PrePersist
    private void initDefaults() {
        this.status = ReadingSessionStatus.ACTIVE;
        this.accumulatedTime = 0;
        this.lastResumedAt = LocalDateTime.now();
    }

    /* ===== 비즈니스 메서드 ===== */

    public void pause() {
        if (status != ReadingSessionStatus.ACTIVE) return;

        int delta = (int) java.time.Duration
                .between(lastResumedAt, LocalDateTime.now())
                .getSeconds();

        this.accumulatedTime += Math.max(delta, 0);
        this.status = ReadingSessionStatus.PAUSED;
    }

    public void resume() {
        if (status != ReadingSessionStatus.PAUSED) return;
        this.lastResumedAt = LocalDateTime.now();
        this.status = ReadingSessionStatus.ACTIVE;
    }

    public int end() {
        if (status == ReadingSessionStatus.ACTIVE) {
            pause();
        }
        this.status = ReadingSessionStatus.ENDED;
        this.endedAt = LocalDateTime.now();
        return this.accumulatedTime;
    }
}
