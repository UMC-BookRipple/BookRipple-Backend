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
                @Index(name = "idx_reading_record_book_created", columnList = "book_id, created_at"),
                @Index(name = "idx_reading_record_member_book_created", columnList = "member_id, book_id, created_at")
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

    @Column(name = "start_page", nullable = false)
    private int startPage;

    @Column(name = "end_page", nullable = false)
    private int endPage;

    @Lob
    @Column(name = "content", columnDefinition = "TEXT")
    private String content;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "book_id", nullable = false)
    private Book book;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    private void validatePages() {
        if (startPage <= 0 || endPage <= 0) {
            throw new IllegalArgumentException("startPage/endPage must be positive.");
        }
        if (startPage > endPage) {
            throw new IllegalArgumentException("startPage must be <= endPage.");
        }
    }
}
