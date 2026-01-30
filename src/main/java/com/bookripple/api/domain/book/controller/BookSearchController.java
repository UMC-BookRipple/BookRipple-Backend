package com.bookripple.api.domain.book.controller;



import com.bookripple.api.common.code.CommonSuccessCode;
import com.bookripple.api.common.response.ApiResponse;
import com.bookripple.api.domain.book.dto.BookRes;
import com.bookripple.api.domain.book.dto.BookSearchRes;
import com.bookripple.api.domain.book.service.BookQueryService;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;


@RestController
@AllArgsConstructor
//@Validated
@RequestMapping(value = "/api/v1/books")

public class BookSearchController {
    private final BookQueryService bookQueryService;

    // 1) 알라딘 검색 API
    @GetMapping("/aladin/search")
    public ApiResponse<BookSearchRes> search(
            @RequestParam @NotBlank @Size(max = 100) String keyword,
            @RequestParam(defaultValue = "1") int start,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "Keyword") String queryType,
            @RequestParam(defaultValue = "Book") String searchTarget
    ) {
        return ApiResponse.onSuccess(CommonSuccessCode.OK,
                bookQueryService.searchFromAladin(keyword, start, size, queryType, searchTarget)
        );
    }

    // 2) 알라딘 도서 상세 조회 API
    @GetMapping("/aladin/{aladinItemId}")
    public ApiResponse<BookRes> getOrCreateByAladinItemId(
            @PathVariable("aladinItemId") Long itemId
    ) {
        return ApiResponse.onSuccess(CommonSuccessCode.OK,
                bookQueryService.getOrCreateByAladinItemId(itemId));
    }
}
