package com.bookripple.api.domain.book.controller;

import com.bookripple.api.global.dto.GlobalDto;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/books/likes")
class BookLikeController {

    private final BookLikeService bookLikeService;

    @PostMapping("/{bookId}")
    public GlobalDto.SingleRes<BookLikeRes> likeBook(
            @PathVariable Long bookId,
            @AuthenticationPrincipal CustomUserDetails user
    ) {
        return GlobalDto.SingleRes.of(
                bookLikeService.like(bookId, user.getId())
        );
    }

    @DeleteMapping("/{bookId}")
    public GlobalDto.SingleRes<BookLikeRes> unlikeBook(
            @PathVariable Long bookId,
            @AuthenticationPrincipal CustomUserDetails user
    ) {
        return GlobalDto.SingleRes.of(
                bookLikeService.unlike(bookId, user.getId())
        );
    }
}

