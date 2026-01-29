package com.bookripple.api.domain.question.repository;

import com.bookripple.api.domain.question.entity.SearchQuestionLog;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SearchQuestionLogRepository extends JpaRepository<SearchQuestionLog, Long> {

  List<SearchQuestionLog> findAllByMemberIdOrderByCreatedAtDesc(Long memberId);

  void deleteAllByMemberId(Long memberId);
}
