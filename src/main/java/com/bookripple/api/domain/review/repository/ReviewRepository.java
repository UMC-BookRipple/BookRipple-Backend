package com.bookripple.api.domain.review.repository;

import com.bookripple.api.domain.review.entity.Review;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
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


}
