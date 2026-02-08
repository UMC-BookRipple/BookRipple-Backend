package com.bookripple.api.domain.question.repository;

import com.bookripple.api.domain.question.entity.Question;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
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
      "   (:onlyMine = true AND q.member.id = :memberId " +
      "    AND EXISTS (SELECT a FROM Answer a WHERE a.question = q AND a.member.id = :memberId)) " +
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

  @Query("SELECT q FROM Question q " +
      "JOIN FETCH q.book " +
      "WHERE q.member.id = :memberId " +
      "AND (:lastBookTitle IS NULL OR " +
      "    q.book.title > :lastBookTitle OR " +
      "    (q.book.title = :lastBookTitle AND q.id < :lastId)) " +
      "ORDER BY q.book.title ASC, q.id DESC "
  )
  Slice<Question> findMyQuestionByCursor(
      @Param("memberId") Long memberId,
      @Param("lastBookTitle") String lastBookTitle,
      @Param("lastId") Long lastId,
      Pageable pageable
  );

  Page<Question> findByBookIdAndContentContainingOrderByIdDesc(Long bookId, String keyword,
      Pageable pageable);

  @Modifying(clearAutomatically = true)
  @Query("DELETE FROM Question q WHERE q.member.id = :memberId AND q.id IN :ids")
  void deleteMyQuestions(@Param("memberId") Long memberId, @Param("ids") List<Long> ids);
}
