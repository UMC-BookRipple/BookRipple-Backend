package com.bookripple.api.domain.memo.controller;

import com.bookripple.api.common.code.CommonSuccessCode;
import com.bookripple.api.common.response.ApiResponse;
import com.bookripple.api.domain.memo.dto.MemoReqDto.Create;
import com.bookripple.api.domain.memo.dto.MemoResDto.MemoList;
import com.bookripple.api.domain.memo.service.MemoCommandService;
import com.bookripple.api.domain.memo.service.MemoQueryService;
import com.bookripple.api.global.dto.GlobalDto.IdRes;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@Validated
@RequestMapping("/api/v1/books/{bookId}/memos")
public class BookMemoController {

    private final MemoQueryService memoQueryService;
    private final MemoCommandService memoCommandService;

    // 메모 생성:
    @PostMapping
    public ApiResponse<IdRes> createMemo(
            @AuthenticationPrincipal Long memberId,
            @PathVariable Long bookId,
            @Valid @RequestBody Create req
    ) {
        return ApiResponse.onSuccess(CommonSuccessCode.OK,
                memoCommandService.createMemo(memberId, bookId, req)
        );
    }

    // 책 별 메모 목록 조회
    @GetMapping
    public ApiResponse<MemoList> getBookMemos(
            @AuthenticationPrincipal Long memberId,
            @PathVariable Long bookId,
            @RequestParam(required = false) Long lastId,
            @RequestParam(defaultValue = "20") int size
    ) {
        return ApiResponse.onSuccess(CommonSuccessCode.OK,
                memoQueryService.getBookMemos(memberId, bookId, lastId, size)
        );
    }
}
