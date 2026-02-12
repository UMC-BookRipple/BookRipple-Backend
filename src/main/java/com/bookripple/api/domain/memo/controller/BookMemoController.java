package com.bookripple.api.domain.memo.controller;

import com.bookripple.api.domain.memo.dto.MemoReqDto.Create;
import com.bookripple.api.domain.memo.dto.MemoResDto.MemoList;
import com.bookripple.api.domain.memo.service.MemoCommandService;
import com.bookripple.api.domain.memo.service.MemoQueryService;
import com.bookripple.api.global.code.CommonSuccessCode;
import com.bookripple.api.global.dto.GlobalDto.IdRes;
import com.bookripple.api.global.response.ApiResponse;
import com.bookripple.api.global.validation.ValidationGroups;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
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

@RestController
@RequiredArgsConstructor
@Tag(name = "독서 메모", description = "독서 메모 생성 및 조회 API")
@RequestMapping("/api/v1/books/{bookId}/memos")
public class BookMemoController {

  private final MemoQueryService memoQueryService;
  private final MemoCommandService memoCommandService;

  // 메모 생성:
  @PostMapping
  @Operation(summary = "책 별 메모 생성", description = "특정 책에 대한 메모를 생성합니다.")
  public ApiResponse<IdRes> createMemo(

      @AuthenticationPrincipal Long memberId,
      @PathVariable Long bookId,
      @Validated(ValidationGroups.MemoGroup.class)
      @RequestBody Create req
  ) {
    return ApiResponse.onSuccess(CommonSuccessCode.OK,
        memoCommandService.createMemo(memberId, bookId, req)
    );
  }

  // 책 별 메모 목록 조회
  @GetMapping
  @Operation(summary = "책 별 메모 목록 조회", description = "특정 책에 대한 메모 목록을 조회합니다.")
  public ApiResponse<MemoList> getBookMemos(
      @AuthenticationPrincipal Long memberId,
      @PathVariable Long bookId,
      @RequestParam(required = false) Long lastId,
      @RequestParam(defaultValue = "20") int size
  ) {
    return ApiResponse.onSuccess(CommonSuccessCode.OK,
        memoQueryService.getBookMemos(memberId, bookId, lastId, size)
    );
  }

  @GetMapping("/me")
  @Operation(
      summary = "내가 쓴 책 별 메모 목록 조회",
      description = "특정 책에 대해 내가 쓴 메모 목록을 조회합니다."
  )
  public ApiResponse<MemoList> getMyBookMemos(
      @AuthenticationPrincipal Long memberId,
      @PathVariable Long bookId,
      @RequestParam(required = false) Long lastId,
      @RequestParam(defaultValue = "20") int size
  ) {
    return ApiResponse.onSuccess(CommonSuccessCode.OK,
        memoQueryService.getMyBookMemos(memberId, bookId, lastId, size)
    );
  }

}
