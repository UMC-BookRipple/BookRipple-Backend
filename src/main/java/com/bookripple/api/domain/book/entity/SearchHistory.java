package com.bookripple.api.domain.book.entity;

import com.bookripple.api.domain.book.enums.SearchLogType;
import com.bookripple.api.domain.member.entity.Member;
import com.bookripple.api.global.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class SearchHistory extends BaseEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private SearchLogType type;

    @Column(nullable = false, length = 100)
    private String keyword;

}

