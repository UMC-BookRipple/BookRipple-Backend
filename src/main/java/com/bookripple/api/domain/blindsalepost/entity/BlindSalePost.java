package com.bookripple.api.domain.blindsalepost.entity;

import com.bookripple.api.domain.blindsalepost.enums.BookCondition;
import com.bookripple.api.domain.book.entity.Book;
import com.bookripple.api.domain.member.entity.Member;
import com.bookripple.api.global.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "blind_salepost")
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
    private String title;

    @Column(nullable = false)
    private String quote;

    @Column(nullable = false)
    private Integer price;

    @Enumerated(EnumType.STRING)
    @Column(name = "book_condition", nullable = false)
    private BookCondition bookCondition;


}
