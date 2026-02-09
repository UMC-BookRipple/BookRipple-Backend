package com.bookripple.api.domain.memo.service;

import com.bookripple.api.domain.memo.dto.MemoResDto;
import com.bookripple.api.domain.memo.dto.MemoResDto.Item;
import com.bookripple.api.domain.memo.dto.MemoResDto.MemoList;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;

public interface MemoQueryService {
    MemoList getBookMemos(Long viewerMemberId, Long bookId, Long lastId, int size);
    Item getMemoDetail(Long viewerMemberId, Long memoId);
    MemoResDto.MyMemoList getMyMemos(Long memberId, Long lastMemoId, int size);
    MemoResDto.MemoList getMyBookMemos(Long memberId, Long bookId, Long lastId, int size);

}
