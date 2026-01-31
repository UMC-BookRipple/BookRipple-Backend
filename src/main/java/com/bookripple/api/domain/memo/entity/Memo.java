package com.bookripple.api.domain.memo.entity;

import com.bookripple.api.domain.book.entity.Book;
import com.bookripple.api.domain.member.entity.Member;
import com.bookripple.api.global.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "memo")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
@Builder
public class Memo extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(columnDefinition = "text")
    private String context;

    @Column(columnDefinition = "text", length = 100)
    private String memoTitle;

    @Column(nullable = false, columnDefinition = "text")
    private String page;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "book_id", nullable = false)
    private Book book;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;


    public void update(String context, String memoTitle, String page) {
        if (context != null) this.context = context;
        if (memoTitle != null) this.memoTitle = memoTitle;
        if (page != null) this.page = page;
    }
}
