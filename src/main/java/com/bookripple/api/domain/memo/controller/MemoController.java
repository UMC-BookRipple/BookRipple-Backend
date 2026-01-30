package com.bookripple.api.domain.memo.controller;

import com.bookripple.api.common.code.CommonSuccessCode;
import com.bookripple.api.common.response.ApiResponse;
import com.bookripple.api.domain.memo.dto.MemoReqDto.Update;
import com.bookripple.api.domain.memo.dto.MemoResDto.Item;
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
@RequestMapping("/api/v1/memos")
public class MemoController {

    private final MemoQueryService memoQueryService;
    private final MemoCommandService memoCommandService;

    // 메모 상세 조회
    @GetMapping("/{memoId}")
    public ApiResponse<Item> getMemoDetail(
            @AuthenticationPrincipal Long memberId,
            @PathVariable Long memoId
    ) {
        return ApiResponse.onSuccess(CommonSuccessCode.OK,
                memoQueryService.getMemoDetail(memberId, memoId)
        );
    }

    // 메모 수정
    @PatchMapping("/{memoId}")
    public ApiResponse<IdRes> updateMemo(
            @AuthenticationPrincipal Long memberId,
            @PathVariable Long memoId,
            @Valid @RequestBody Update req
    ) {
        return ApiResponse.onSuccess(CommonSuccessCode.OK,
                memoCommandService.updateMemo(memberId, memoId, req)
        );
    }

    // 메모 삭제
    @DeleteMapping("/{memoId}")
    public ApiResponse<IdRes> deleteMemo(
            @AuthenticationPrincipal Long memberId,
            @PathVariable Long memoId
    ) {
        return ApiResponse.onSuccess(CommonSuccessCode.OK,
                memoCommandService.deleteMemo(memberId, memoId)
        );
    }
}
