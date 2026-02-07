package com.bookripple.api.domain.reading.entity;

import com.bookripple.api.domain.book.entity.Book;
import com.bookripple.api.domain.member.entity.Member;
import com.bookripple.api.global.entity.BaseEntity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(
        name = "reading_record",
        indexes = {
                @Index(name = "idx_reading_record_member_created", columnList = "member_id, created_at"),
                @Index(name = "idx_reading_record_book_created", columnList = "book_id, created_at")
        }
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public class ReadingRecord extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 페이지 입력
    @Column(name = "start_page", nullable = false)
    private int startPage;

    @Column(name = "end_page", nullable = false)
    private int endPage;

    // 총 독서 시간 (분)
    @Column(name = "reading_time", nullable = false)
    private int readingTime;

    @Lob
    @Column(name = "content", nullable = false)
    private String content;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "book_id", nullable = false)
    private Book book;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @PrePersist
    private void initDefaultsAndValidate() {
        // 페이지 기반 기능 미사용 → 우선 값은 1
        if (this.startPage == 0) this.startPage = 1;
        if (this.endPage == 0) this.endPage = 1;

        if (this.readingTime < 0) {
            throw new IllegalArgumentException("readingTime must be >= 0");
        }

        if (this.content == null) {
            this.content = "";
        }
    }

    // lastPage 기반으로 진행률 갱신
    public void updateLastPage(int pagesReadEnd, int totalPages) {
        if (totalPages <= 0) return; // 혹은 예외
        if (pagesReadEnd < 0) pagesReadEnd = 0;
        if (pagesReadEnd > totalPages) pagesReadEnd = totalPages;

        this.endPage = pagesReadEnd;
    }

    public int getProgressPercent(int totalPages) {
        if (totalPages <= 0) return 0;
        int safeLast = Math.min(Math.max(this.endPage, 0), totalPages);

        // 반올림
        return (int) Math.round((safeLast * 100.0) / totalPages);
    }

}
