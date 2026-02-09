package com.bookripple.api.domain.memo.controller;

import com.bookripple.api.common.code.CommonSuccessCode;
import com.bookripple.api.common.response.ApiResponse;
import com.bookripple.api.domain.memo.dto.MemoReqDto.Update;
import com.bookripple.api.domain.memo.dto.MemoResDto;
import com.bookripple.api.domain.memo.dto.MemoResDto.Item;
import com.bookripple.api.domain.memo.service.MemoCommandService;
import com.bookripple.api.domain.memo.service.MemoQueryService;
import com.bookripple.api.global.dto.GlobalDto.IdRes;
import com.bookripple.api.global.validation.ValidationGroups;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.tags.Tags;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@Tag(name = "독서 메모", description = "메모 조회, 수정, 삭제 API")
@RequestMapping("/api/v1/memos")
public class MemoController {



    private final MemoQueryService memoQueryService;
    private final MemoCommandService memoCommandService;

    // 메모 상세 조회
    @GetMapping("/{memoId}")
    @Operation(
            summary = "메모 상세 조회",
            description = "메모의 상세 정보를 조회합니다."
    )
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
    @Operation(
            summary = "메모 수정",
            description = "메모의 내용을 수정합니다."
    )
    public ApiResponse<IdRes> updateMemo(
            @AuthenticationPrincipal Long memberId,
            @PathVariable Long memoId,
            @Validated(ValidationGroups.MemoGroup.class)
            @RequestBody Update req
    ) {
        return ApiResponse.onSuccess(CommonSuccessCode.OK,
                memoCommandService.updateMemo(memberId, memoId, req)
        );
    }

    // 메모 삭제
    @DeleteMapping("/{memoId}")
    @Operation(
            summary = "메모 삭제",
            description = "메모를 삭제합니다."
    )
    public ApiResponse<IdRes> deleteMemo(
            @AuthenticationPrincipal Long memberId,
            @PathVariable Long memoId
    ) {
        return ApiResponse.onSuccess(CommonSuccessCode.OK,
                memoCommandService.deleteMemo(memberId, memoId)
        );
    }

    @GetMapping("/me")
    @Operation(
            summary = "내 메모 목록 조회",
            description = "내가 작성한 메모 목록을 조회합니다."
    )
    public ApiResponse<MemoResDto.MyMemoList> getMyMemos(
            @AuthenticationPrincipal Long memberId,
            @RequestParam(required = false) @Min(1) Long lastMemoId,
            @RequestParam(defaultValue = "20") int size
    ) {
        return ApiResponse.onSuccess(CommonSuccessCode.OK,
                memoQueryService.getMyMemos(memberId, lastMemoId, size));
    }

}
