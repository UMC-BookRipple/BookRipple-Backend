package com.bookripple.api.domain.library.service;

import java.util.List;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bookripple.api.domain.library.dto.LibraryItemListRes;
import com.bookripple.api.domain.library.dto.LibraryItemRes;
import com.bookripple.api.domain.library.entity.LibraryItem;
import com.bookripple.api.domain.library.enums.LibraryStatus;
import com.bookripple.api.domain.library.repository.LibraryItemRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class LibraryQueryServiceImpl implements LibraryQueryService {

    private final LibraryItemRepository libraryItemRepository;

    @Override
    public LibraryItemListRes getMyLibrary(Long memberId, LibraryStatus status, Long lastId, int size) {

        Pageable pageable = PageRequest.of(0, size + 1);

        List<LibraryItem> fetched = (lastId == null)
                ? libraryItemRepository.findByMemberIdAndStatusOrderByIdDesc(memberId, status, pageable)
                : libraryItemRepository.findByMemberIdAndStatusAndIdLessThanOrderByIdDesc(
                memberId, status, lastId, pageable
        );

        boolean hasNext = fetched.size() > size;
        if (hasNext) {
            fetched = fetched.subList(0, size);
        }

        List<LibraryItemRes> items = fetched.stream()
                .map(LibraryItemRes::from)
                .toList();

        Long nextLastId = fetched.isEmpty() ? null : fetched.get(fetched.size() - 1).getId();

        return LibraryItemListRes.of(items, hasNext, nextLastId);
    }


}
