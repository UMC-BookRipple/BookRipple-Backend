package com.bookripple.api.domain.blindsalepost.dto;

public class PurchaseRequestReqDto {
    // 구매 요청 시 필요한 정보. ID만 받아도 된다.
    public record Create(
            Long blindPostId
    ) {}
}
