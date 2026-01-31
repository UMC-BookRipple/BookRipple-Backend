package com.bookripple.api.domain.memo.service;

import com.bookripple.api.domain.memo.dto.MemoResDto.Item;
import com.bookripple.api.domain.memo.dto.MemoResDto.MemoList;

public interface MemoQueryService {
    MemoList getBookMemos(Long viewerMemberId, Long bookId, Long lastId, int size);
    Item getMemoDetail(Long viewerMemberId, Long memoId);
}
