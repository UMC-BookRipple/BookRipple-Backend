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

    //  [상세 조회] 모든 상황을 아우르는 상세 정보
    @Builder
    public record Detail(
            Long blindBookId,
            String title,
            String subtitle,
            String description,
            Integer price,
            String status,     // 게시글 상태 (SALE, DONE 등)
            Long requestCount, // 판매요청 인원 수
            List<PurchaseRequestInfo> requests // 구매 요청자 명단
    ) {}

    // 상세 조회 내부에 포함될 구매 요청자 정보
    @Builder
    public record PurchaseRequestInfo(
            Long requestId,
            String name,    // 구매 요청한 사람 이름
            String status   // WAITING, ACCEPTED 등
    ) {}
}
