package com.bookripple.api.domain.library.controller;

import com.bookripple.api.common.code.CommonSuccessCode;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.bookripple.api.domain.library.dto.LibraryItemListRes;
import com.bookripple.api.domain.library.enums.LibraryStatus;
import com.bookripple.api.domain.library.service.LibraryQueryService;
import com.bookripple.api.common.response.ApiResponse;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@Validated
@RequestMapping("api/v1/library")
public class LibraryController {

    private final LibraryQueryService libraryQueryService;

    // 1. 내 책장 조회 API
    @GetMapping("/books")
    public ApiResponse<LibraryItemListRes> getMyLibrary(
            @AuthenticationPrincipal Long memberId,
            @RequestParam LibraryStatus status,
            @RequestParam(required = false) Long lastId,
            @RequestParam(defaultValue = "20") int size
    ) {
        return ApiResponse.onSuccess(CommonSuccessCode.OK,
                libraryQueryService.getMyLibrary(memberId, status, lastId, size)
        );
    }
}


