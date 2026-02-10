package com.bookripple.api.domain.library.service;

import com.bookripple.api.domain.library.dto.LibraryBookDetailRes;
import com.bookripple.api.domain.library.dto.LibraryBookSummaryListRes;
import com.bookripple.api.domain.library.dto.LibraryDto;
import com.bookripple.api.domain.library.dto.LibraryItemListRes;
import com.bookripple.api.domain.library.dto.LibraryItemRes;
import com.bookripple.api.domain.library.enums.LibraryStatus;

public interface LibraryQueryService {

    LibraryItemListRes getMyLibrary(
            Long memberId,
            LibraryStatus status,
            Long lastId,
            int size
    );

    LibraryDto.DeleteRes deleteBooks(Long memberId, LibraryStatus status, LibraryDto.DeleteReq request);

    LibraryBookDetailRes getMyLibraryBookDetail(Long memberId, Long bookId);

    /**
     * 마이페이지에서 표시할 읽고 있는 책의 요약 정보를 조회합니다.
     * @param memberId 사용자 ID
     * @return 읽고 있는 책(READING) 요약 정보 목록
     */
    LibraryBookSummaryListRes getMyLibraryBooksSummary(Long memberId);
}
