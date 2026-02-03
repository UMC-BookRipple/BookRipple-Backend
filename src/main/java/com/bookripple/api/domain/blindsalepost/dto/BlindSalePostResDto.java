package com.bookripple.api.domain.blindsalepost.dto;

import lombok.Builder;

import java.time.LocalDateTime;
import java.util.List;

public class BlindSalePostResDto {
    //  등록 성공 응답
    @Builder
    public record Create(
            Long blindBookId,
            String status,         // SALE 등
            LocalDateTime createdAt
    ) {}
    // [목록 조회] 탭별 리스트에 들어갈 요약 정보
    @Builder
    public record ListElement(
            Long blindBookId,
            String title,      // 블라인드 제목
            String author,     // 저자 (실제 도서 정보에서 추출)
            Integer price,
            String status      // SALE, DONE 등
    ) {}

    // 무한 스크롤(Slice)을 위한 응답 묶음
    @Builder
    public record SliceResponse(
            List<ListElement> content,
            Long nextCursor,
            boolean hasNext
    ) {}

    // [화면 1] 게시글 상세 정보 (판매자용)
    @Builder
    public record Detail(
            Long blindBookId,
            String title,
            String subtitle,
            String description,
            Integer price,
            String status,
            Long requestCount // "판매요청 3명"을 띄우기 위한 카운트
    ) {}

    // [화면 2] 구매 요청자 목록
    @Builder
    public record PurchaseRequestList(
            Long blindBookId,
            List<PurchaseRequestInfo> requests
    ) {}

    // 상세 조회 내부에 포함될 구매 요청자 정보
    @Builder
    public record PurchaseRequestInfo(
            Long requestId,
            String name,    // "익명의 사용자 1325" 등
            String status   // WAITING, ACCEPTED 등
    ) {}
}
