package com.bookripple.api.domain.book.controller;


import com.bookripple.api.common.code.CommonSuccessCode;
import com.bookripple.api.common.response.ApiResponse;
import com.bookripple.api.domain.book.dto.BookRes;
import com.bookripple.api.domain.book.dto.BookSearchRes;
import com.bookripple.api.domain.book.service.BookQueryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


@RestController
@AllArgsConstructor
@Tag(
    name = "도서 검색 및 상세 조회",
    description = "알라딘 도서 검색 및 도서 상세 조회 API")
@RequestMapping(value = "/api/v1/books")

public class BookSearchController {

  private final BookQueryService bookQueryService;

  // 1) 알라딘 검색 API
  @GetMapping("/aladin/search")
  @Operation(
      summary = "알라딘 도서 검색",
      description = "알라딘 오픈 API를 활용한 도서 검색 기능입니다."
  )
  public ApiResponse<BookSearchRes> search(
          @AuthenticationPrincipal Long memberId,
      @RequestParam @NotBlank @Size(max = 100) String keyword,
      @RequestParam(defaultValue = "1") int start,
      @RequestParam(defaultValue = "20") int size,
      @RequestParam(defaultValue = "Keyword") String queryType,
      @RequestParam(defaultValue = "Book") String searchTarget
  ) {
    return ApiResponse.onSuccess(CommonSuccessCode.OK,
        bookQueryService.searchFromAladin(memberId, keyword, start, size, queryType, searchTarget)
    );
  }

  // 2) 알라딘 도서 상세 조회 API
  @GetMapping("/aladin/{aladinItemId}")
  @Operation(
      summary = "알라딘 도서 상세 조회",
      description = "알라딘 도서 상세 정보를 조회하고, DB로 가져옵니다."
  )
  public ApiResponse<BookRes> getOrCreateByAladinItemId(
      @PathVariable("aladinItemId") Long itemId
  ) {
    return ApiResponse.onSuccess(CommonSuccessCode.OK,
        bookQueryService.getOrCreateByAladinItemId(itemId));
  }

  @GetMapping("/itemNewSpecial")
  public ApiResponse<BookSearchRes> getSpecialNewBooks() {
    return ApiResponse.onSuccess(CommonSuccessCode.OK,
        bookQueryService.getSpecialNewBooks());
  }
}
