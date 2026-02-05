package com.bookripple.api.domain.book.service;

import com.bookripple.api.domain.book.dto.SearchHistoryRes;
import com.bookripple.api.domain.book.entity.SearchHistory;
import com.bookripple.api.domain.book.repository.SearchHistoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SearchHistoryQueryService {

    private final SearchHistoryRepository searchHistoryRepository;

    public SearchHistoryRes.ListRes getHistories(Long memberId, Long lastId, int size) {
        Pageable pageable = PageRequest.of(0, size);

        List<SearchHistory> list = (lastId == null)
                ? searchHistoryRepository.findByMemberIdOrderByIdDesc(memberId, pageable)
                : searchHistoryRepository.findByMemberIdAndIdLessThanOrderByIdDesc(memberId, lastId, pageable);

        List<SearchHistoryRes.Item> items = list.stream()
                .map(h -> new SearchHistoryRes.Item(h.getId(), h.getKeyword(), h.getCreatedAt()))
                .toList();

        Long nextLastId = items.isEmpty() ? null : items.get(items.size() - 1).getHistoryId();
        boolean hasNext = items.size() == size;

        return new SearchHistoryRes.ListRes(items, nextLastId, hasNext);
    }
}

