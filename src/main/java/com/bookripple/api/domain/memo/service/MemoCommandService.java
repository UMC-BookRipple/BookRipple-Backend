package com.bookripple.api.domain.memo.service;

import com.bookripple.api.domain.memo.dto.MemoReqDto.Create;
import com.bookripple.api.domain.memo.dto.MemoReqDto.Update;
import com.bookripple.api.global.dto.GlobalDto.IdRes;

public interface MemoCommandService {
    IdRes createMemo(Long memberId, Long bookId, Create req);
    IdRes updateMemo(Long memberId, Long memoId, Update req);
    IdRes deleteMemo(Long memberId, Long memoId);
}
