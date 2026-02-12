package com.bookripple.api.domain.library.controller;

import com.bookripple.api.domain.library.dto.LibraryBookDetailRes;
import com.bookripple.api.domain.library.dto.LibraryBookSummaryListRes;
import com.bookripple.api.domain.library.dto.LibraryDto;
import com.bookripple.api.domain.library.dto.LibraryItemListRes;
import com.bookripple.api.domain.library.enums.LibraryStatus;
import com.bookripple.api.domain.library.service.LibraryQueryService;
import com.bookripple.api.global.annotation.PreventDuplicate;
import com.bookripple.api.global.code.CommonSuccessCode;
import com.bookripple.api.global.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Tag(
    name = "내 책장",
    description = "내 책장 조회 및 도서 삭제 API")
@RestController
@RequiredArgsConstructor
@Validated
@RequestMapping("api/v1/library")
public class LibraryController {

  private final LibraryQueryService libraryQueryService;

  // 1. 내 책장 조회 API
  @GetMapping("/books")
  @Operation(
      summary = "내 책장 도서 조회",
      description = "내 책장에 담긴 도서들을 상태별[LIKED, READING, COMPLETED]로 조회합니다."
  )
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

  // 2. 내 책장 도서 삭제 API
  @PreventDuplicate
  @PostMapping("/books/delete")
  @Operation(
      summary = "내 책장 도서 삭제",
      description = "내 책장에서 도서를 선택 삭제합니다."
  )
  public ApiResponse<LibraryDto.DeleteRes> deleteBooks(
      @AuthenticationPrincipal Long memberId,
      @RequestParam LibraryStatus status,
      @Parameter(description = "삭제할 도서의 bookId 리스트", example = "[1, 2, 3]")
      @Valid @RequestBody LibraryDto.DeleteReq request

  ) {
    return ApiResponse.onSuccess(
        CommonSuccessCode.OK,
        libraryQueryService.deleteBooks(memberId, status, request)
    );
  }

  //3. 내 책장 도서 상세 조회 API
  @GetMapping("/books/{bookId}")
  @Operation(
      summary = "내 책장 도서 상세 조회",
      description = "내 책장 도서 상세정보와 진행률을 조회합니다."
  )
  public ApiResponse<LibraryBookDetailRes> getMyLibraryBookDetail(
      @AuthenticationPrincipal Long memberId,
      @PathVariable Long bookId
  ) {
    return ApiResponse.onSuccess(
        CommonSuccessCode.OK,
        libraryQueryService.getMyLibraryBookDetail(memberId, bookId)
    );
  }

  //4. 마이페이지 도서 목록 조회 API
  @GetMapping("/books-summary")
  @Operation(
      summary = "마이페이지 도서 요약 목록 조회 (읽고 있는 책)",
      description = "마이페이지에서 표시할 읽고 있는(READING) 책들의 요약 정보를 조회합니다. " +
          "각 책의 bookId, coverUrl, title, authors, status, progressPercent, " +
          "readingTimeMinutes, estimatedDaysToCompletion 정보를 반환합니다."
  )
  public ApiResponse<LibraryBookSummaryListRes> getMyLibraryBooksSummary(
      @AuthenticationPrincipal Long memberId
  ) {
    return ApiResponse.onSuccess(
        CommonSuccessCode.OK,
        libraryQueryService.getMyLibraryBooksSummary(memberId)
    );
  }

}


