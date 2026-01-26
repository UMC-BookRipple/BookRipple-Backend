package com.bookripple.api.domain.recommendation.repository;

import com.bookripple.api.domain.recommendation.entity.Recommendation;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface RecommendRepository extends JpaRepository<Recommendation, Long> {

  @Query("SELECT rec FROM Recommendation rec " +
      "JOIN FETCH rec.member " +
      "JOIN FETCH rec.targetBook " +
      "JOIN FETCH rec.sourceBook " +
      "WHERE rec.sourceBook.id = :bookId " +
      "AND (:memberId IS NULL OR rec.member.id != :memberId)" +
      "AND rec.id < :lastId " +
      "ORDER BY rec.id DESC")
  Slice<Recommendation> findRecommendationByCursor(
      @Param("bookId") Long bookId,
      @Param("memberId") Long memberId,
      @Param("lastId") Long lastId,
      Pageable pageable
  );

  @Query("SELECT rec FROM Recommendation rec " +
      "JOIN FETCH rec.sourceBook " +
      "JOIN FETCH rec.targetBook " +
      "WHERE rec.member.id = :memberId " +
      "AND (:lastSourceBookTitle IS NULL OR " +
      "    rec.sourceBook.title > :lastSourceBookTitle OR " +
      "    (rec.sourceBook.title = :lastSourceBookTitle AND rec.id < :lastId)) " +
      "ORDER BY rec.sourceBook.title ASC, rec.id DESC ")
  Slice<Recommendation> findMyRecommendationByCursor(
      @Param("memberId") Long memberId,
      @Param("lastSourceBookTitle") String lastSourceBookTitle,
      @Param("lastId") Long lastId,
      Pageable pageable
  );
}
