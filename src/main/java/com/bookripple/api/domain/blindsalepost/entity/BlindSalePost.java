package com.bookripple.api.domain.blindsalepost.entity;

import com.bookripple.api.domain.blindsalepost.enums.BookCondition;
import com.bookripple.api.domain.blindsalepost.enums.PostStatus;
import com.bookripple.api.domain.book.entity.Book;
import com.bookripple.api.domain.member.entity.Member;
import com.bookripple.api.global.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "blind_sale_post")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public class BlindSalePost extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "book_id", nullable = false)
    private Book book;

    @Column(nullable = false)
    private String title;       // 게시글 제목

    @Column(nullable = false)
    private String subtitle;     // 포스트잇 문구 (부제목)

    @Column(nullable = false)
    private String description;  // 상세 판매 문구

    @Column(nullable = false)
    private Integer price;

    @Enumerated(EnumType.STRING)
    @Column(name = "book_condition", nullable = false)
    private BookCondition bookCondition;

    @Builder.Default
    @Enumerated(EnumType.STRING)
    @Column(name = "post_status", nullable = false)
    private PostStatus postStatus = PostStatus.SALE;

    // 수정(Update) 메서드도 함께 업데이트해 줍니다.
    public void update(String title, String subtitle, String description, Integer price, BookCondition condition) {
        this.title = title;
        this.subtitle = subtitle;
        this.description = description;
        this.price = price;
        this.bookCondition = condition;
    }

}
