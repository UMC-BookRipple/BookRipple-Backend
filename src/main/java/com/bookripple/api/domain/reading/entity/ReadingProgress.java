package com.bookripple.api.domain.reading.entity;

import java.math.BigDecimal;

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

    @Column(name = "reading_time", nullable = false)
    private int readingTime;

    @Column(name = "last_page", nullable = false)
    private int lastPage;

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

    // 초기값 설정
    @PrePersist
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

    public void uncomplete() {
        this.isCompleted = false;
        this.progress = BigDecimal.ZERO;
    }

    public void toggleLiked(boolean liked) {
        this.isLiked = liked;
    }

    // lastPage 기반으로 진행률 갱신
    public void updateLastPage(int pagesReadEnd, int totalPages) {
        if (totalPages <= 0) return; // 혹은 예외
        if (pagesReadEnd < 0) pagesReadEnd = 0;
        if (pagesReadEnd > totalPages) pagesReadEnd = totalPages;

        this.lastPage = pagesReadEnd;

        if (this.lastPage >= totalPages) {
            this.isCompleted = true;
        }
    }

    public int getProgressPercent(int totalPages) {
        if (totalPages <= 0) return 0;
        int safeLast = Math.min(Math.max(this.lastPage, 0), totalPages);

        // 반올림
        return (int) Math.round((safeLast * 100.0) / totalPages);
    }

}
