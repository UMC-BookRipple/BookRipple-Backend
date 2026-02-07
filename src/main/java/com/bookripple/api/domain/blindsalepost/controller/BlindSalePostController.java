package com.bookripple.api.domain.blindsalepost.controller;

import com.bookripple.api.common.code.CommonSuccessCode;
import com.bookripple.api.common.response.ApiResponse;
import com.bookripple.api.domain.blindsalepost.dto.BlindSalePostReqDto;
import com.bookripple.api.domain.blindsalepost.dto.BlindSalePostResDto;
import com.bookripple.api.domain.blindsalepost.enums.PostStatus;
import com.bookripple.api.domain.blindsalepost.service.BlindSalePostService;
import com.bookripple.api.global.annotation.PreventDuplicate;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Tag( name = "Blind Sale Post", description = "블라인드 북 판매 게시글 관련 API")
@RestController
@RequestMapping("/api/v1/blind-books") // API 리스트에 정의된 공통 경로
@RequiredArgsConstructor
public class BlindSalePostController {

    // 인터페이스 타입으로 주입받아 유연성을 높입니다
    private final BlindSalePostService blindSalePostService;

    // 판매자용 판매 도서 등록
    @Operation(summary = "블라인드 북 판매 게시글 생성", description = "판매자용 판매")
    @PreventDuplicate
    @PostMapping
    public ApiResponse<BlindSalePostResDto.Create> create(
            @AuthenticationPrincipal Long memberId,
            @RequestBody BlindSalePostReqDto.Create requestDto) {

        BlindSalePostResDto.Create response = blindSalePostService.createPost(memberId, requestDto);
        return ApiResponse.onSuccess(CommonSuccessCode.CREATED, response);
    }

    // 판매자용 내 판매 목록 조회 (무한 스크롤)
    @Operation(summary = "내 블라인드 북 판매 게시글 목록 조회", description = "판매자용 내 판매 목록 조회 (무한 스크롤)")
    @GetMapping("/me")
    public ApiResponse<BlindSalePostResDto.SliceResponse> getMyList(
            @AuthenticationPrincipal Long memberId,
            @RequestParam PostStatus status,
            @RequestParam(required = false) Long cursor,
            @RequestParam(defaultValue = "10") int size) {

        BlindSalePostResDto.SliceResponse response =
                blindSalePostService.getMyPostList(memberId, status, cursor, size);
        return ApiResponse.onSuccess(CommonSuccessCode.OK, response);
    }

    // 판매자용 판매 도서 상세 정보 조회 (기본 상세 페이지)
    @Operation(summary = "블라인드 북 판매 게시글 상세 조회", description = "판매자용 글 상세 정보 조회")
    @GetMapping("/{blind-book-id}")
    public ApiResponse<BlindSalePostResDto.Detail> getPostDetail(
            @PathVariable("blind-book-id") Long blindBookId) {

        BlindSalePostResDto.Detail response = blindSalePostService.getPostDetail(blindBookId);
        return ApiResponse.onSuccess(CommonSuccessCode.OK, response);
    }

    // 판매자용 구매 요청자 명단 조회 (판매요청 인원 클릭 시)
    @Operation(summary = "구매 요청자 명단 조회", description = "판매자용 구매 요청자 명단 조회")
    @GetMapping("/{blind-book-id}/requests")
    public ApiResponse<BlindSalePostResDto.PurchaseRequestList> getPurchaseRequests(
            @PathVariable("blind-book-id") Long blindBookId) {

        BlindSalePostResDto.PurchaseRequestList response = blindSalePostService.getPurchaseRequests(blindBookId);
        return ApiResponse.onSuccess(CommonSuccessCode.OK, response);
    }

    // 판매자용 글 수정하기
    @Operation(summary = "블라인드 북 판매 게시글 수정", description = "판매자용 글 수정하기")
    @PreventDuplicate
    @PatchMapping("/{blind-book-id}")
    public ApiResponse<String> update(
            @AuthenticationPrincipal Long memberId,
            @PathVariable("blind-book-id") Long blindBookId,
            @RequestBody BlindSalePostReqDto.Update requestDto) {

        blindSalePostService.updatePost(memberId, blindBookId, requestDto);
        return ApiResponse.onSuccess(CommonSuccessCode.OK, "게시글 수정이 완료되었습니다.");
    }

    // 판매자용 글 삭제하기
    @Operation(summary = "블라인드 북 판매 게시글 삭제", description = "판매자용 글 삭제하기")
    @PreventDuplicate
    @DeleteMapping("/{blind-book-id}")
    public ApiResponse<String> delete(
            @AuthenticationPrincipal Long memberId,
            @PathVariable("blind-book-id") Long blindBookId) {

        blindSalePostService.deletePost(memberId, blindBookId);
        return ApiResponse.onSuccess(CommonSuccessCode.OK, "게시글이 성공적으로 삭제되었습니다.");
    }

    // [GET] /api/v1/blind-books?status=SALE
    // 구매자용 블라인드 북 판매 목록 조회 (무한 스크롤)
    @Operation(summary = "구매자용 블라인드 북 판매 게시글 목록 조회", description = "구매자용 블라인드 북 판매 목록 조회 (무한 스크롤)")
    @GetMapping
    public ApiResponse<BlindSalePostResDto.BuyerSliceResponse<BlindSalePostResDto.BuyerListElement>> getAllPosts(
            @RequestParam(value = "status", required = false) String status,
            @RequestParam(required = false) Long cursor,
            @RequestParam(defaultValue = "10") int size) {

        // 서비스 호출 시 제네릭이 적용된 슬라이스 응답을 반환합니다.
        BlindSalePostResDto.BuyerSliceResponse<BlindSalePostResDto.BuyerListElement> response =
                blindSalePostService.getAllPosts(cursor, size);

        return ApiResponse.onSuccess(CommonSuccessCode.OK, response);
    }

    // [GET] /api/v1/blind-books/my-requests
    // 구매자용 내가 구매요청 보낸 책 목록 조회 (무한 스크롤)
    @Operation(summary = "내가 구매 요청한 블라인드 북 목록 조회", description = "구매자용 내가 구매요청 보낸 책 목록 조회 (무한 스크롤)")
    @GetMapping("/my-requests")
    public ApiResponse<BlindSalePostResDto.BuyerSliceResponse<BlindSalePostResDto.MyRequestListElement>> getMyRequests(
            @AuthenticationPrincipal Long memberId,
            @RequestParam(required = false) Long cursor,
            @RequestParam(defaultValue = "10") int size) {

        return ApiResponse.onSuccess(CommonSuccessCode.OK,
                blindSalePostService.getMyRequests(memberId, cursor, size));
    }

    // [GET] /api/v1/blind-books/{blind-book-id}/buyer
    @Operation(summary = "구매자용 블라인드 북 판매 게시글 상세 조회", description = "구매자용 블라인드 북 판매 게시글 상세 정보 조회")
    @GetMapping("/{blind-book-id}/buyer")
    public ApiResponse<BlindSalePostResDto.BuyerDetail> getPostDetailForBuyer(
            @AuthenticationPrincipal Long memberId,
            @PathVariable("blind-book-id") Long blindBookId) {

        return ApiResponse.onSuccess(CommonSuccessCode.OK,
                blindSalePostService.getPostDetailForBuyer(memberId, blindBookId));
    }
}