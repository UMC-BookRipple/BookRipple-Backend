package com.bookripple.api.domain.reading.entity;

import java.math.BigDecimal;
import java.math.RoundingMode;

import com.bookripple.api.domain.book.entity.Book;
import com.bookripple.api.domain.member.entity.Member;
import com.bookripple.api.global.entity.BaseEntity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(
        name = "reading_progress",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_reading_progress_member_book",
                        columnNames = {"member_id", "book_id"}
                )
        },
        indexes = {
                @Index(name = "idx_reading_progress_member", columnList = "member_id"),
                @Index(name = "idx_reading_progress_book", columnList = "book_id")
        }
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public class ReadingProgress extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Setter
    @Column(name = "reading_time", nullable = false)
    private int readingTime;

    @Setter
    @Column(name = "progress", nullable = false, precision = 5, scale = 2)
    private BigDecimal progress;

    @Setter
    @Column(name = "is_liked", nullable = false)
    private boolean isLiked;

    @Setter
    @Column(name = "is_completed", nullable = false)
    private boolean isCompleted;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "book_id", nullable = false)
    private Book book;

    @PrePersist
    // 초기값 설정
    private void initDefaults() {
        this.readingTime = 0;
        this.progress = BigDecimal.ZERO;
        this.isLiked = false;
        this.isCompleted = false;
    }

    public void addReadingTime(int seconds) {
        if (seconds > 0) {
            this.readingTime += seconds;
        }
    }

    public void markCompleted() {
        this.isCompleted = true;
        this.progress = BigDecimal.valueOf(100);
    }

    public void toggleLiked(boolean liked) {
        this.isLiked = liked;
    }

    public void applyRecord(ReadingRecord record, int totalPages) {
        if (totalPages <= 0) return;

        int end = clamp(record.getEndPage(), totalPages);

        this.progress = BigDecimal.valueOf(end)
                .multiply(BigDecimal.valueOf(100))
                .divide(BigDecimal.valueOf(totalPages), 2, RoundingMode.DOWN);

        if (end >= totalPages) {
            this.isCompleted = true;
            this.progress = BigDecimal.valueOf(100);
        }
    }

    private int clamp(int v, int max) {
        return Math.min(Math.max(v, 0), max);
    }


}
