package com.bookripple.api.domain.book.service;

import com.bookripple.api.domain.book.dto.SearchHistoryRes;
import com.bookripple.api.domain.book.entity.SearchHistory;
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

    public void saveOrRefresh(Long memberId, String keyword) {

        searchHistoryRepository.deleteByMemberIdAndKeyword(memberId, keyword);

        SearchHistory history = SearchHistory.builder()
                .member(memberRepository.getReferenceById(memberId))
                .keyword(keyword)
                .build();

        searchHistoryRepository.save(history);
    }

    public SearchHistoryRes.DeleteOne deleteOne(Long memberId, Long historyId) {
        long deleted = searchHistoryRepository.deleteByIdAndMemberId(historyId, memberId);
        return new SearchHistoryRes.DeleteOne(deleted == 1);
    }

    public SearchHistoryRes.DeleteAll deleteAll(Long memberId) {
        long deletedCount = searchHistoryRepository.deleteByMemberId(memberId);
        return new SearchHistoryRes.DeleteAll(deletedCount);
    }
}
