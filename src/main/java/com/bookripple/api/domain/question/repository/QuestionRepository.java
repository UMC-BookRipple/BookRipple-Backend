package com.bookripple.api.domain.question.repository;

import com.bookripple.api.domain.question.entity.Question;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface QuestionRepository extends JpaRepository<Question, Long> {

  @Query("SELECT q FROM Question q " +
      "WHERE q.book.id = :bookId " +
      "AND (:lastId IS NULL OR q.id < :lastId) " +
      "AND (:keyword IS NULL OR q.content LIKE CONCAT('%', :keyword, '%')) " +
      "AND (" +
      "   (:onlyMine = true AND q.member.id = :memberId) " +
      "   OR " +
      "   (:onlyMine = false AND q.member.id != :memberId) " +
      ") " +
      "ORDER BY q.id DESC")
  Slice<Question> findQuestionByCursor(
      @Param("bookId") Long bookId,
      @Param("lastId") Long lastId,
      @Param("memberId") Long memberId,
      @Param("keyword") String keyword,
      @Param("onlyMine") boolean onlyMine,
      Pageable pageable
  );

  @Query("SELECT COUNT(q) FROM Question q " +
      "WHERE q.book.id = :bookId " +
      "AND (:keyword IS NULL OR q.content LIKE CONCAT('%', :keyword, '%')) " +
      "AND (" +
      "   (:onlyMine = true AND q.member.id = :memberId) " +
      "   OR " +
      "   (:onlyMine = false AND q.member.id != :memberId) " +
      ") ")
  long countQuestions(
      @Param("bookId") Long bookId,
      @Param("memberId") Long memberId,
      @Param("keyword") String keyword,
      @Param("onlyMine") boolean onlyMine
  );
}
