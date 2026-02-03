package com.bookripple.api.domain.book.controller;

import com.bookripple.api.common.code.CommonSuccessCode;
import com.bookripple.api.common.response.ApiResponse;
import com.bookripple.api.domain.book.dto.BookLikeRes;
import com.bookripple.api.domain.book.service.BookCommandService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(
        name = "도서 좋아요",
        description = "도서 좋아요 추가/삭제 API"
)
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

