package com.bookripple.api.domain.book.entity;

import com.bookripple.api.global.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Table(
        name = "book",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_book_isbn", columnNames = "isbn"),
                @UniqueConstraint(name = "uk_book_aladin_book_id", columnNames = "aladin_book_id")
        }
)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
@Builder
public class Book extends BaseEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  Long id;

  @Column(nullable = false)
  private String story;

  @Column(nullable = false)
  String title;

  @Column(nullable = false)
  String author;
  
  @Column(name = "cover_url", nullable = false)
  String bookCover;

  @Column(nullable = false)
  private LocalDate year;

  @Column(length = 13, unique = true)
  private String isbn;

  @Column(nullable = false)
  private String publisher;

  @Column(nullable = false)
  private Integer totalPage;

  // aladin_book_id bigint NULL UNIQUE
  @Column(unique = true)
  private Long aladinBookId;
}
