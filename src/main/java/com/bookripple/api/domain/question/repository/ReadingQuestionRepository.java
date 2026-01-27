package com.bookripple.api.domain.question.repository;

import com.bookripple.api.domain.question.entity.ReadingQuestion;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReadingQuestionRepository extends JpaRepository<ReadingQuestion, Long> {

  Slice<ReadingQuestion> findByMemberIdAndBookIdAndIdLessThanOrderByIdDesc(
      Long memberId,
      Long bookId,
      Long lastId,
      Pageable pageable
  );
}
