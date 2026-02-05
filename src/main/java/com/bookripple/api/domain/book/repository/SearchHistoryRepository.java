package com.bookripple.api.domain.book.repository;

import com.bookripple.api.domain.book.entity.SearchHistory;
import com.bookripple.api.domain.book.enums.SearchLogType;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SearchHistoryRepository extends JpaRepository<SearchHistory, Long> {
    List<SearchHistory> findByMemberIdAndTypeAndIdLessThanOrderByIdDesc(
            Long memberId, SearchLogType type, Long lastId, Pageable pageable
    );

    List<SearchHistory> findByMemberIdAndTypeOrderByIdDesc(
            Long memberId, SearchLogType type, Pageable pageable
    );


    // 단건 삭제
    Optional<SearchHistory> findByIdAndMemberIdAndType(Long id, Long memberId, SearchLogType type);


    // 전체 삭제
    long deleteByMemberIdAndType(Long memberId, SearchLogType type);


    // 중복 키워드 최신화
    void deleteByMemberIdAndTypeAndKeyword(Long memberId, SearchLogType type, String keyword);

    long deleteByIdAndMemberIdAndType(Long historyId, Long memberId, SearchLogType type);
}
