package com.bookripple.api.domain.question.repository;

import com.bookripple.api.domain.question.entity.Answer;
import java.util.List;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface AnswerRepository extends JpaRepository<Answer, Long> {

  List<Answer> findAllByQuestionId(Long questionId);

  @Query("SELECT a FROM Answer a " +
      "JOIN FETCH a.question " +
      "WHERE a.member.id = :memberId " +
      "AND a.id < :lastId " +
      "ORDER BY a.id DESC ")
  Slice<Answer> findMyAnswerByCursor(
      @Param("memberId") Long memberId,
      @Param("lastId") Long lastId,
      Pageable pageable
  );
}
