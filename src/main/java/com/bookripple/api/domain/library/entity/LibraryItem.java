package com.bookripple.api.domain.library.entity;

import com.bookripple.api.domain.book.entity.Book;
import com.bookripple.api.domain.library.enums.LibraryStatus;
import com.bookripple.api.domain.member.entity.Member;
import com.bookripple.api.global.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "library_item",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_library_member_book", columnNames = {"member_id", "book_id"})
        }
)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
@Builder
public class LibraryItem extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "book_id", nullable = false)
    private Book book;

    @Setter
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private LibraryStatus status;

}
