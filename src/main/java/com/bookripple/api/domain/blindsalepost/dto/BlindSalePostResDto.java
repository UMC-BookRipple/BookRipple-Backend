package com.bookripple.api.domain.blindsalepost.dto;

import lombok.Builder;

import java.time.LocalDateTime;

public class BlindSalePostResDto {
    // 1. 등록 성공 응답
    @Builder
    public record Create(
            Long blindBookId,
            String status,         // SALE 등
            LocalDateTime createdAt
    ) {}
}
