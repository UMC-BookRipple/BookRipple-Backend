package com.bookripple.api.domain.question.repository;

import com.bookripple.api.domain.question.entity.Answer;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AnswerRepository extends JpaRepository<Answer, Long> {

  List<Answer> findAllByQuestionId(Long questionId);
}
