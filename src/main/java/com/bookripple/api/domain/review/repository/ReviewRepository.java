package com.bookripple.api.domain.review.repository;

import com.bookripple.api.domain.review.entity.Review;
import java.util.List;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {

  @Query("SELECT r FROM Review r " +
      "JOIN FETCH r.member " +
      "WHERE r.book.id = :bookId " +
      "AND (:memberId IS NULL OR r.member.id != :memberId)" +
      "AND r.id < :lastId " +
      "ORDER BY r.id DESC")
  Slice<Review> findReviewByCursor(
      @Param("bookId") Long bookId,
      @Param("memberId") Long memberId,
      @Param("lastId") Long lastId,
      Pageable pageable
  );

  @Query("SELECT r FROM Review r " +
      "JOIN FETCH r.book " +
      "WHERE r.member.id = :memberId " +
      "AND (:lastBookTitle IS NULL OR " +
      "    r.book.title > :lastBookTitle OR " +
      "    (r.book.title = :lastBookTitle AND r.id < :lastId)) " +
      "ORDER BY r.book.title ASC, r.id DESC "
  )
  Slice<Review> findMyReviewByCursor(
      @Param("memberId") Long memberId,
      @Param("lastBookTitle") String lastBookTitle,
      @Param("lastId") Long lastId,
      Pageable pageable
  );

  @Modifying(clearAutomatically = true)
  @Query("DELETE FROM Review r WHERE r.member.id = :memberId AND r.id IN :ids")
  void deleteMyReviews(@Param("memberId") Long memberId, @Param("ids") List<Long> ids);
}
