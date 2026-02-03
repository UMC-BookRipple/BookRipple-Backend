package com.bookripple.api.domain.blindsalepost.controller;

import com.bookripple.api.common.code.CommonSuccessCode;
import com.bookripple.api.common.response.ApiResponse;
import com.bookripple.api.domain.blindsalepost.dto.BlindSalePostReqDto;
import com.bookripple.api.domain.blindsalepost.dto.BlindSalePostResDto;
import com.bookripple.api.domain.blindsalepost.enums.PostStatus;
import com.bookripple.api.domain.blindsalepost.service.BlindSalePostService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/blind-books") // API 리스트에 정의된 공통 경로
@RequiredArgsConstructor
public class BlindSalePostController {

    // 인터페이스 타입으로 주입받아 유연성을 높입니다
    private final BlindSalePostService blindSalePostService;

    // 1. 판매 도서 등록
    @PostMapping
    public ApiResponse<BlindSalePostResDto.Create> create(
            @AuthenticationPrincipal Long memberId,
            @RequestBody BlindSalePostReqDto.Create requestDto) {

        BlindSalePostResDto.Create response = blindSalePostService.createPost(memberId, requestDto);
        return ApiResponse.onSuccess(CommonSuccessCode.CREATED, response);
    }

    // 2. 내 판매 목록 조회 (무한 스크롤)
    @GetMapping
    public ApiResponse<BlindSalePostResDto.SliceResponse> getMyList(
            @AuthenticationPrincipal Long memberId,
            @RequestParam PostStatus status,
            @RequestParam(required = false) Long cursor,
            @RequestParam(defaultValue = "10") int size) {

        BlindSalePostResDto.SliceResponse response =
                blindSalePostService.getMyPostList(memberId, status, cursor, size);
        return ApiResponse.onSuccess(CommonSuccessCode.OK, response);
    }

    // 3. 판매 도서 상세 정보 조회 (기본 상세 페이지)
    @GetMapping("/{blind-book-id}")
    public ApiResponse<BlindSalePostResDto.Detail> getPostDetail(
            @PathVariable("blind-book-id") Long blindBookId) {

        BlindSalePostResDto.Detail response = blindSalePostService.getPostDetail(blindBookId);
        return ApiResponse.onSuccess(CommonSuccessCode.OK, response);
    }

    // 4. 구매 요청자 명단 조회 (판매요청 인원 클릭 시)
    @GetMapping("/{blind-book-id}/requests")
    public ApiResponse<BlindSalePostResDto.PurchaseRequestList> getPurchaseRequests(
            @PathVariable("blind-book-id") Long blindBookId) {

        BlindSalePostResDto.PurchaseRequestList response = blindSalePostService.getPurchaseRequests(blindBookId);
        return ApiResponse.onSuccess(CommonSuccessCode.OK, response);
    }

    // 5. 글 수정하기
    @PatchMapping("/{blind-book-id}")
    public ApiResponse<String> update(
            @AuthenticationPrincipal Long memberId,
            @PathVariable("blind-book-id") Long blindBookId,
            @RequestBody BlindSalePostReqDto.Update requestDto) {

        blindSalePostService.updatePost(memberId, blindBookId, requestDto);
        return ApiResponse.onSuccess(CommonSuccessCode.OK, "게시글 수정이 완료되었습니다.");
    }

    // 6. 글 삭제하기
    @DeleteMapping("/{blind-book-id}")
    public ApiResponse<String> delete(
            @AuthenticationPrincipal Long memberId,
            @PathVariable("blind-book-id") Long blindBookId) {

        blindSalePostService.deletePost(memberId, blindBookId);
        return ApiResponse.onSuccess(CommonSuccessCode.OK, "게시글이 성공적으로 삭제되었습니다.");
    }
}