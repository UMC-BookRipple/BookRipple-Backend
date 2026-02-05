package com.bookripple.api.domain.book.controller;

import com.bookripple.api.common.code.CommonSuccessCode;
import com.bookripple.api.common.response.ApiResponse;
import com.bookripple.api.domain.book.dto.SearchHistoryRes;
import com.bookripple.api.domain.book.enums.SearchLogType;
import com.bookripple.api.domain.book.service.SearchHistoryCommandService;
import com.bookripple.api.domain.book.service.SearchHistoryQueryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Tag(
        name = "도서 검색 기록",
        description = "사용자 검색 기록 조회 및 삭제 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/books/search")

public class SearchHistoryController {

    private final SearchHistoryQueryService queryService;
    private final SearchHistoryCommandService commandService;

    @GetMapping("/history")
    @Operation(
            summary = "검색 기록 조회",
            description = "사용자의 검색 기록을 조회합니다. "
    )
    public ApiResponse<SearchHistoryRes.ListRes> getHistories(
            @AuthenticationPrincipal Long memberId,
            @RequestParam(defaultValue = "BOOK") SearchLogType type,
            @RequestParam(required = false) Long lastId,
            @RequestParam(defaultValue = "20") int size
    ) {
        return ApiResponse.onSuccess(CommonSuccessCode.OK, queryService.getHistories(memberId, type, lastId, size));
    }

    @DeleteMapping("/history/{historyId}")
    @Operation(
            summary = "검색 기록 삭제 (단일)",
            description = "사용자의 특정 검색 기록을 삭제합니다."
    )
    public ApiResponse<SearchHistoryRes.DeleteOne> deleteOne(
            @AuthenticationPrincipal Long memberId,
            @RequestParam(defaultValue = "BOOK") SearchLogType type,
            @PathVariable Long historyId
    ) {
        return ApiResponse.onSuccess(CommonSuccessCode.OK, commandService.deleteOne(memberId, type, historyId));
    }

    @DeleteMapping("/history")
    @Operation(
            summary = "검색 기록 삭제 (전체)",
            description = "사용자의 모든 검색 기록을 삭제합니다."
    )
    public ApiResponse<SearchHistoryRes.DeleteAll> deleteAll(
            @AuthenticationPrincipal Long memberId,
            @RequestParam(defaultValue = "BOOK") SearchLogType type

    ) {
        return ApiResponse.onSuccess(CommonSuccessCode.OK, commandService.deleteAll(memberId, type));
    }
}

