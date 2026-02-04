package com.bookripple.api.domain.book.controller;

import com.bookripple.api.common.code.CommonSuccessCode;
import com.bookripple.api.common.response.ApiResponse;
import com.bookripple.api.domain.book.dto.BookLikeRes;
import com.bookripple.api.domain.book.service.BookCommandService;
import io.swagger.v3.oas.annotations.Operation;
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
    @Operation(
            summary = "도서 좋아요 추가",
            description = "사용자가 특정 도서를 좋아요 처리합니다."
    )
    public ApiResponse<BookLikeRes> likeBook(
            @PathVariable Long bookId,
            @AuthenticationPrincipal Long memberId
    ) {
        return ApiResponse.onSuccess(CommonSuccessCode.OK,
                bookCommandService.likeBook(bookId, memberId));
    }

    @DeleteMapping("/{bookId}")
    @Operation(
            summary = "도서 좋아요 취소",
            description = "사용자가 좋아요한 도서를 취소합니다."
    )
    public ApiResponse<BookLikeRes> unlikeBook(
            @PathVariable Long bookId,
            @AuthenticationPrincipal Long memberId
    ) {
        return ApiResponse.onSuccess(CommonSuccessCode.OK,
                bookCommandService.unlikeBook(bookId, memberId)
        );
    }
}

