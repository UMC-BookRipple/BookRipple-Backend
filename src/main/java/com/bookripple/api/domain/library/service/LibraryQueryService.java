package com.bookripple.api.domain.library.service;

import com.bookripple.api.domain.library.dto.LibraryItemListRes;
import com.bookripple.api.domain.library.dto.LibraryReq;
import com.bookripple.api.domain.library.dto.LibraryRes;
import com.bookripple.api.domain.library.enums.LibraryStatus;

public interface LibraryQueryService {

    LibraryItemListRes getMyLibrary(
            Long memberId,
            LibraryStatus status,
            Long lastId,
            int size
    );

    LibraryRes.Delete deleteBooks(Long memberId, LibraryReq.Delete request);
}
