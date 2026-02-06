package com.bookripple.api.domain.library.service;

import com.bookripple.api.domain.library.dto.LibraryDto;
import com.bookripple.api.domain.library.dto.LibraryItemListRes;
import com.bookripple.api.domain.library.enums.LibraryStatus;

public interface LibraryQueryService {

    LibraryItemListRes getMyLibrary(
            Long memberId,
            LibraryStatus status,
            Long lastId,
            int size
    );

    LibraryDto.DeleteRes deleteBooks(Long memberId, LibraryStatus status, LibraryDto.DeleteReq request);
}
