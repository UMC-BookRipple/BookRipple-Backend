package com.bookripple.api.domain.blindsalepost.dto;

public class BlindSalePostReqDto {
    public record Create(
            Long actualBookId,     // 검색으로 선택한 실제 책 ID
            String title,          // 사용자가 입력한 블라인드 제목
            String blindContent,   // 엔티티의 quote 필드에 매핑
            String bookCondition,  // "상", "중", "하" 등 상태
            Integer price          // 가격
    ) {}
}
