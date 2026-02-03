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
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/blind-books") // API 리스트에 정의된 공통 경로
@RequiredArgsConstructor
public class BlindSalePostController {

    // 인터페이스 타입으로 주입받아 유연성을 높입니다
    private final BlindSalePostService blindSalePostService;

    @PostMapping // 판매 도서 등록(게시글 만들기)
    public ResponseEntity<BlindSalePostResDto.Create> create(
            @RequestBody BlindSalePostReqDto.Create requestDto) {

        /* * 실제 운영 환경에서는 Spring Security 등을 통해
         * 로그인한 유저의 ID를 가져오겠지만,
         * 현재는 구현 흐름을 잡기 위해 임시 ID(1L)를 사용합니다.
         */
        Long loginMemberId = 1L;

        // 서비스를 호출하여 비즈니스 로직을 실행하고 결과를 받습니다
        BlindSalePostResDto.Create response = blindSalePostService.createPost(loginMemberId, requestDto);

        // 생성 성공 시 201 Created 상태 코드와 함께 응답 DTO를 반환합니다
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // [GET] /api/v1/blind-books
    @GetMapping
    public ResponseEntity<BlindSalePostResDto.SliceResponse> getMyList(
            @RequestParam PostStatus status,             // 판매중/거래완료 탭 필터
            @RequestParam(required = false) Long cursor, // 이전 페이지의 마지막 게시글 ID
            @RequestParam(defaultValue = "10") int size) {

        // 현재 로그인 유저 ID (추후 시큐리티로 교체 예정)
        Long loginMemberId = 1L;

        // 서비스 호출 시 로그인 유저 ID를 함께 넘겨줍니다.
        BlindSalePostResDto.SliceResponse response =
                blindSalePostService.getMyPostList(loginMemberId, status, cursor, size);

        return ResponseEntity.ok(response);
    }

    // 판매 도서 상세 정보 조회 (기본 상세 페이지)
    @GetMapping("/{blind-book-id}")
    public ApiResponse<BlindSalePostResDto.Detail> getPostDetail(
            @PathVariable("blind-book-id") Long blindBookId) {

        BlindSalePostResDto.Detail response = blindSalePostService.getPostDetail(blindBookId);
        return ApiResponse.onSuccess(CommonSuccessCode.OK, response);
    }

    // 구매 요청자 명단 조회 (판매요청 인원 클릭 시)
    @GetMapping("/{blind-book-id}/requests")
    public ApiResponse<BlindSalePostResDto.PurchaseRequestList> getPurchaseRequests(
            @PathVariable("blind-book-id") Long blindBookId) {

        BlindSalePostResDto.PurchaseRequestList response = blindSalePostService.getPurchaseRequests(blindBookId);
        return ApiResponse.onSuccess(CommonSuccessCode.OK, response);
    }

    // 글 수정하기 버튼 매핑
    @PatchMapping("/{blind-book-id}")
    public ResponseEntity<String> update(
            @PathVariable("blind-book-id") Long blindBookId,
            @RequestBody BlindSalePostReqDto.Update requestDto) {

        // 현재 로그인 유저 ID (임시 1L) 전달
        blindSalePostService.updatePost(1L, blindBookId, requestDto);

        return ResponseEntity.ok("게시글 수정이 완료되었습니다.");
    }

    // [DELETE] /api/v1/blind-books/{id}
    @DeleteMapping("/{blind-book-id}")
    public ResponseEntity<String> delete(
            @PathVariable("blind-book-id") Long blindBookId) {

        // 현재 로그인 유저 ID (추후 시큐리티 적용 전까지 임시 1L 사용)
        Long loginMemberId = 1L;

        blindSalePostService.deletePost(loginMemberId, blindBookId);

        return ResponseEntity.ok("게시글이 성공적으로 삭제되었습니다.");
    }
}