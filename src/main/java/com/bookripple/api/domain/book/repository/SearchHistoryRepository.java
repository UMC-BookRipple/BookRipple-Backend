package com.bookripple.api.domain.book.repository;

import com.bookripple.api.domain.book.entity.SearchHistory;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SearchHistoryRepository extends JpaRepository<SearchHistory, Long> {
    List<SearchHistory> findByMemberIdAndIdLessThanOrderByIdDesc(Long memberId, Long lastId, Pageable pageable);
    List<SearchHistory> findByMemberIdOrderByIdDesc(Long memberId, Pageable pageable);

    // 단건 삭제
    long deleteByIdAndMemberId(Long id, Long memberId);

    // 전체 삭제
    long deleteByMemberId(Long memberId);

    // 중복 키워드 최신화
    void deleteByMemberIdAndKeyword(Long memberId, String keyword);
}
