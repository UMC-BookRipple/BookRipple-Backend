package com.bookripple.api.domain.book.service;

import com.bookripple.api.domain.book.dto.SearchHistoryRes;
import com.bookripple.api.domain.book.entity.SearchHistory;
import com.bookripple.api.domain.book.enums.SearchLogType;
import com.bookripple.api.domain.book.repository.SearchHistoryRepository;
import com.bookripple.api.domain.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class SearchHistoryCommandService {

    private final SearchHistoryRepository searchHistoryRepository;
    private final MemberRepository memberRepository;

    public void saveOrRefresh(Long memberId, String keyword, SearchLogType type) {
        if (memberId == null) return;

        String normalized = (keyword == null) ? "" : keyword.trim();
        if (normalized.isBlank()) return;

        searchHistoryRepository.deleteByMemberIdAndTypeAndKeyword(memberId, type, normalized);

        SearchHistory history = SearchHistory.builder()
                .member(memberRepository.getReferenceById(memberId))
                .keyword(normalized)
                .type(type)
                .build();

        searchHistoryRepository.save(history);
    }

    public SearchHistoryRes.DeleteOne deleteOne(Long memberId, SearchLogType type, Long historyId) {
        long deleted = searchHistoryRepository.deleteByIdAndMemberIdAndType(historyId, memberId, type);
        return new SearchHistoryRes.DeleteOne(deleted == 1);
    }

    public SearchHistoryRes.DeleteAll deleteAll(Long memberId, SearchLogType type) {
        long deletedCount = searchHistoryRepository.deleteByMemberIdAndType(memberId, type);
        return new SearchHistoryRes.DeleteAll(deletedCount);
    }
}