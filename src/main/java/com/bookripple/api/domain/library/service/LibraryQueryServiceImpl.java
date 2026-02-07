package com.bookripple.api.domain.library.service;

import java.util.List;

import com.bookripple.api.common.code.LibraryErrorCode;
import com.bookripple.api.common.error.ApiException;
import com.bookripple.api.domain.library.dto.LibraryBookDetailRes;
import com.bookripple.api.domain.library.dto.LibraryDto;
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
@Transactional
public class LibraryQueryServiceImpl implements LibraryQueryService {

    private final LibraryItemRepository libraryItemRepository;
    private final ReadingProgressRepository readingProgressRepository;

    @Override
    public LibraryItemListRes getMyLibrary(Long memberId, LibraryStatus status, Long lastId, int size) {
        Pageable pageable = PageRequest.of(0, size);

        //like 기준 조회
        if (status == LibraryStatus.LIKED) {

            List<ReadingProgress> fetched = (lastId == null)
                    ? readingProgressRepository.findByMemberIdAndIsLikedTrueOrderByIdDesc(memberId, pageable)
                    : readingProgressRepository.findByMemberIdAndIsLikedTrueAndIdLessThanOrderByIdDesc(memberId, lastId, pageable);

            boolean hasNext = fetched.size() > size;
            if (hasNext) {
                fetched = fetched.subList(0, size);
            }

            List<LibraryItemRes> items = fetched.stream()
                    .map(LibraryItemRes::from)
                    .toList();

            Long nextLastId = fetched.isEmpty() ? null : fetched.get(fetched.size() - 1).getId(); // rp.id

            return LibraryItemListRes.of(items, hasNext, nextLastId);
        }

        // reading, completed 기준 조회하는 경우
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
    public LibraryDto.DeleteRes deleteBooks(Long memberId, LibraryStatus status, LibraryDto.DeleteReq request) {

        long deleted = libraryItemRepository.deleteByMemberIdAndStatusAndBook_IdIn(
                memberId,
                status,
                request.getBookIds()
        );

        return LibraryDto.DeleteRes.of(deleted);
    }

    @Override
    public LibraryBookDetailRes getMyLibraryBookDetail(Long memberId, Long bookId) {
        LibraryItem item = libraryItemRepository.findByMemberIdAndBook_Id(memberId, bookId)
                .orElseThrow(() -> new ApiException(LibraryErrorCode.NO_LIBRARY_BOOK));


        ReadingProgress progress = readingProgressRepository.findByMemberIdAndBookId(memberId, bookId);

        return LibraryBookDetailRes.of(item, progress);
    }
}
