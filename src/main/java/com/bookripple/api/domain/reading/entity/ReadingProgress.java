package com.bookripple.api.domain.reading.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

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
                @Index(name = "idx_reading_progress_book", columnList = "book_id"),
                @Index(name = "idx_reading_progress_updated_at", columnList = "updated_at")
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

    @Column(name = "current_page", nullable = false)
    private int currentPage;

    @Column(name = "reading_time", nullable = false)
    private int readingTime;

    @Column(name = "progress", nullable = false, precision = 5, scale = 2)
    private BigDecimal progress;

    @Column(name = "is_liked", nullable = false)
    private boolean isLiked;

    @Column(name = "is_completed", nullable = false)
    private boolean isCompleted;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "book_id", nullable = false)
    private Book book;


    // 진행률 계산 메서드
    public void updateByEndPage(int endPage) {
        if (endPage > this.currentPage) {
            this.currentPage = endPage;
        }

        int totalPage = book.getTotalPage();
        BigDecimal pct = BigDecimal.valueOf(this.currentPage)
                .multiply(BigDecimal.valueOf(100))
                .divide(BigDecimal.valueOf(totalPage), 2, java.math.RoundingMode.HALF_UP);

        // 백분율 계산
        if (pct.compareTo(BigDecimal.ZERO) < 0) pct = BigDecimal.ZERO;
        if (pct.compareTo(BigDecimal.valueOf(100)) > 0) pct = BigDecimal.valueOf(100);

        this.progress = pct;
        this.isCompleted = (this.progress.compareTo(BigDecimal.valueOf(100)) >= 0);
    }

    public void addReadingTime(int minutesToAdd) {
        if (minutesToAdd <= 0) return;
        this.readingTime += minutesToAdd;
    }

    public void toggleLiked(boolean liked) {
        this.isLiked = liked;
    }
}
