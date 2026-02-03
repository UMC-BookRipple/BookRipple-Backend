package com.bookripple.api.domain.book.controller;

import com.bookripple.api.common.code.CommonSuccessCode;
import com.bookripple.api.common.response.ApiResponse;
import com.bookripple.api.domain.book.dto.BookLikeRes;
import com.bookripple.api.domain.book.service.BookCommandService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/books/likes")
class BookLikeController {

    private final BookCommandService bookCommandService;

    @PostMapping("/{bookId}")
    public ApiResponse<BookLikeRes> likeBook(
            @PathVariable Long bookId,
            @AuthenticationPrincipal Long memberId
    ) {
        return ApiResponse.onSuccess(CommonSuccessCode.OK,
                bookCommandService.likeBook(bookId, memberId));
    }

    @DeleteMapping("/{bookId}")
    public ApiResponse<BookLikeRes> unlikeBook(
            @PathVariable Long bookId,
            @AuthenticationPrincipal Long memberId
    ) {
        return ApiResponse.onSuccess(CommonSuccessCode.OK,
                bookCommandService.unlikeBook(bookId, memberId)
        );
    }
}

