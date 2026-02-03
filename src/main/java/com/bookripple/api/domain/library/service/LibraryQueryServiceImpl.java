package com.bookripple.api.domain.library.service;

import java.util.List;

import com.bookripple.api.domain.library.dto.LibraryReq;
import com.bookripple.api.domain.library.dto.LibraryRes;
import com.bookripple.api.domain.reading.entity.ReadingProgress;
import com.bookripple.api.domain.reading.repository.ReadingProgressRepository;
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
    private final ReadingProgressRepository readingProgressRepository;

    @Override
    public LibraryItemListRes getMyLibrary(Long memberId, LibraryStatus status, Long lastId, int size) {
        Pageable pageable = PageRequest.of(0, size);
        if (status == LibraryStatus.LIKED) {
            List<ReadingProgress> liked = (lastId == null)
                    ? readingProgressRepository.findByMemberIdAndIsLikedTrueOrderByIdDesc(memberId, pageable)
                    : readingProgressRepository.findByMemberIdAndIsLikedTrueAndIdLessThanOrderByIdDesc(memberId, lastId, pageable);

            // 여기서 rp.getBook() 기반으로 응답 DTO 매핑
            return LibraryConverter.fromLikedProgress(liked);
        }
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

    @Override
    public LibraryRes.Delete deleteBooks(Long memberId, LibraryReq.Delete request) {

        long deleted = libraryItemRepository.deleteByMemberIdAndStatusAndBook_IdIn(
                memberId,
                request.getCategory(),
                request.getBookIds()
        );

        return LibraryRes.Delete.of(deleted);
    }
}
