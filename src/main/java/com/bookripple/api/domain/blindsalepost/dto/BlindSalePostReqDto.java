package com.bookripple.api.domain.blindsalepost.dto;

import lombok.Builder;

public class BlindSalePostReqDto {
    @Builder
    public record Create(
            Long actualBookId,
            String title,
            String subtitle,    // 추가: 포스트잇 내용
            String description, // 추가: 상세 설명
            String bookCondition,
            Integer price
    ) {}

    @Builder
    public record Update(
            String title,
            String subtitle,    // 추가
            String description, // 추가
            String bookCondition,
            Integer price
    ) {}
}
