package com.bookripple.api.domain.memo.dto;

import com.bookripple.api.global.dto.GlobalDto.ContentReq;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class MemoReqDto {

    public record Create(
            @Valid ContentReq contentReq,
            @Size(max = 100) String memoTitle,
            @NotBlank String page
    ) {}

    public record Update(
            @Valid ContentReq contentReq,  // 생본문 수정 가능
            @Size(max = 100) String memoTitle,
            String page
    ) {}
}
